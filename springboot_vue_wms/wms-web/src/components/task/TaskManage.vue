<template>
  <div>
    <!-- 搜索栏 -->
    <div style="margin-bottom: 5px;">
      <el-input
        v-model="searchTitle"
        placeholder="请输入任务标题"
        suffix-icon="el-icon-search"
        style="width: 200px;"
        @keyup.enter.native="loadTasks"
      ></el-input>
      <el-select
        v-model="searchStatus"
        filterable
        placeholder="请选择状态"
        style="margin-left: 5px; width: 150px;"
        clearable
      >
        <el-option
          v-for="item in statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-select
        v-model="searchAssignee"
        filterable
        placeholder="请选择分配人"
        style="margin-left: 5px; width: 150px;"
        clearable
      >
        <el-option
          v-for="user in userList"
          :key="user.id"
          :label="user.name"
          :value="user.id"
        ></el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 5px;" @click="loadTasks">查询</el-button>
      <el-button type="success" @click="resetSearch">重置</el-button>
      <el-button type="primary" style="margin-left: 5px;" @click="openAddDialog">新增任务</el-button>
      <el-button size="small" type="info" @click="viewDetail">任务详情</el-button>
    </div>

    <!-- 任务表格 -->
    <el-table
      :data="tableData"
      :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
      border
    >
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="description" label="描述" width="200" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creatorName" label="创建人" width="120" />
      <el-table-column prop="assigneeName" label="分配人" width="120" />
      <el-table-column prop="deadline" label="截止时间" width="160" />
      <el-table-column prop="operate" label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-popconfirm
            title="确定删除该任务吗？"
            @confirm="del(scope.row.id)"
            style="margin-left: 5px;"
          >
            <el-button slot="reference" size="small" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[5, 10, 20, 30]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top: 10px;"
    ></el-pagination>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="form.id ? '编辑任务' : '新增任务'"
      :visible.sync="dialogVisible"
      width="40%"
      center
    >
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分配人" prop="assignee_id">
          <el-select v-model="form.assignee_id" placeholder="请选择分配人">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            placeholder="选择截止时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%;"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "TaskManage",
  data() {
    return {
      tableData: [],
      pageSize: 10,
      pageNum: 1,
      total: 0,
      searchTitle: "",
      searchStatus: "",
      searchAssignee: "",
      statusOptions: [
        { value: 0, label: "未开始" },
        { value: 1, label: "分析设计" },
        { value: 2, label: "基本搭建" },
        { value: 3, label: "项目开发" },
        { value: 4, label: "测试" },
        { value: 5, label: "完成" }
      ],
      stateCount: {
        0: 0, // 未开始
        1: 0, // 分析设计
        2: 0, // 基本搭建
        3: 0, // 项目开发
        4: 0, // 测试
        5: 0  // 完成
      },
      userList: [],
      dialogVisible: false,
      form: {
        id: "",
        title: "",
        description: "",
        status: 0,
        creator_id: "", // 当前登录用户id，实际使用时应自动填充
        assignee_id: "",
        deadline: ""
      },
      rules: {
        title: [
          { required: true, message: "请输入任务标题", trigger: "blur" },
          { min: 2, max: 100, message: "长度在 2 到 100 个字符", trigger: "blur" }
        ],
        status: [
          { required: true, message: "请选择状态", trigger: "change" }
        ],
        assignee_id: [
          { required: true, message: "请选择分配人", trigger: "change" }
        ],
        deadline: [
          { required: true, message: "请选择截止时间", trigger: "change" }
        ]
      }
    };
  },
  methods: {
    // 状态标签类型
    statusTagType(status) {
      switch (status) {
        case 0: return "info";
        case 1: return "warning";
        case 2: return "primary";
        case 3: return "primary";
        case 4: return "success";
        case 5: return "success";
        default: return "";
      }
    },
    // 状态标签文本
    statusLabel(status) {
      const found = this.statusOptions.find(item => item.value === status);
      return found ? found.label : "";
    },
    // 获取所有用户（分配人下拉用）
    loadUsers() {
      this.$axios.get(this.$httpUrl + "/user/listAll").then(res => res.data).then(res => {
        if (res.code === 200) {
          this.userList = res.data;
        }
      });
    },
    // 查询任务
    loadTasks() {
      this.$axios.post(this.$httpUrl + "/task/listPage", {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        param: {
          title: this.searchTitle,
          status: this.searchStatus,
          assignee_id: this.searchAssignee
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.tableData = res.data;
          this.total = res.total;
          // 初始化状态统计
          this.stateCount = {
            0: 0,
            1: 0,
            2: 0,
            3: 0,
            4: 0,
            5: 0
          };
          this.tableData.forEach(task => {
            this.stateCount[task.status]++;
          });
        }
      });
    },
    // 新增
    openAddDialog() {
      this.form = {
        id: "",
        title: "",
        description: "",
        status: 0,
        creator_id: "", // 实际应为当前登录用户id
        assignee_id: "",
        deadline: ""
      };
      this.dialogVisible = true;
    },
    // 编辑
    openEditDialog(row) {
      this.form = {
        id: row.id,
        title: row.title,
        description: row.description,
        status: row.status,
        creator_id: row.creator_id,
        assignee_id: row.assignee_id,
        deadline: row.deadline
      };
      this.dialogVisible = true;
    },
    // 保存（新增/编辑）
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        // 新增
        if (!this.form.id) {
          this.form.creator_id = this.$store?.state?.user?.id || 1;
          this.$axios.post(this.$httpUrl + "/task/save", this.form).then(res => res.data).then(res => {
            if (res.code === 200) {
              this.$message.success("新增成功！");
              this.dialogVisible = false;
              this.loadTasks();

              // 更新状态统计
              this.stateCount[this.form.status]++;
            } else {
              this.$message.error("新增失败！");
            }
          });
        } else {
          // 编辑
          this.$axios.post(this.$httpUrl + "/task/update", this.form).then(res => res.data).then(res => {
            if (res.code === 200) {
              this.$message.success("修改成功！");
              this.dialogVisible = false;
              this.loadTasks();

              // 更新状态统计
              const oldStatus = this.tableData.find(task => task.id === this.form.id)?.status;
              if (oldStatus !== undefined && oldStatus !== this.form.status) {
                this.stateCount[oldStatus]--;
                this.stateCount[this.form.status]++;
              }
            } else {
              this.$message.error("修改失败！");
            }
          });
        }
      });
    },
    // 删除
    del(id) {
      this.$axios.get(this.$httpUrl + "/task/del?id=" + id).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.$message.success("删除成功！");
          this.loadTasks();
        } else {
          this.$message.error("删除失败！");
        }
      });
    },
    // 分页
    handleSizeChange(val) {
      this.pageNum = 1;
      this.pageSize = val;
      this.loadTasks();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.loadTasks();
    },
    // 重置搜索
    resetSearch() {
      this.searchTitle = "";
      this.searchStatus = "";
      this.searchAssignee = "";
      this.loadTasks();
    },
    prepareChartData() {
      const chartData = {
        labels: this.statusOptions.map(option => option.label),
        values: this.statusOptions.map(option => this.stateCount[option.value])
      };
      return chartData;
    },
    viewDetail() {
      const chartData = {
        labels: this.statusOptions.map(option => option.label),
        values: this.statusOptions.map(option => this.stateCount[option.value])
      };
      const params = {
        title: this.searchTitle,
        status: this.searchStatus,
        assignee_id: this.searchAssignee,
        chartData: JSON.stringify(this.prepareChartData()) // 字符串化数据
      };
      this.$router.push({
        path: '/task/statistics',
        query: params
      });
    }
  },
  mounted() {
    this.loadUsers();
    this.loadTasks();
  }
};
</script>

<style scoped>
/* 可根据需要自定义样式 */
</style>