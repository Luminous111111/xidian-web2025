<!--员工查看所有的任务 按照状态搜索暂时未完成-->
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
            :value="user.name"
        ></el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 5px;" @click="loadTasks">查询</el-button>
      <el-button type="success" @click="resetSearch">重置</el-button>
<!--      <el-button type="primary" style="margin-left: 5px;" @click="openAddDialog">新增任务</el-button>-->
    </div>

    <!-- 任务表格 -->
    <el-table
        :data="tableData"
        :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
        border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" width="140" />
      <el-table-column prop="description" label="描述" width="220" />
      <el-table-column prop="status" label="进度" width="140">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creatorId" label="创建主管工号" width="140" />
      <el-table-column prop="assigneeName" label="分配人" width="140" />
      <el-table-column prop="deadline" label="截止时间" width="180" />
<!--      <el-table-column prop="operate" label="操作" width="180">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button size="small" type="success" @click="openEditDialog(scope.row)">更新进度</el-button>-->
<!--          <el-popconfirm-->
<!--              title="确定删除该任务吗？"-->
<!--              @confirm="del(scope.row.id)"-->
<!--              style="margin-left: 5px;"-->
<!--          >-->
<!--            <el-button slot="reference" size="small" type="danger">删除</el-button>-->
<!--          </el-popconfirm>-->
<!--        </template>-->
<!--      </el-table-column>-->
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
        :title="form.id ? '更新任务进度' : '新增任务'"
        :visible.sync="dialogVisible"
        width="40%"
        center
    >
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
<!--        <el-form-item label="标题" prop="title">-->
<!--          <el-input v-model="form.title"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="描述" prop="description">-->
<!--          <el-input v-model="form.description" type="textarea"></el-input>-->
<!--        </el-form-item>-->
        <el-form-item label="进度" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="分配人" prop="assigneeName">-->
<!--          <el-select v-model="form.assigneeName" placeholder="请选择分配人">-->
<!--            <el-option-->
<!--                v-for="user in userList"-->
<!--                :key="user.id"-->
<!--                :label="user.name"-->
<!--                :value="user.name"-->
<!--            ></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
<!--        <el-date-picker-->
<!--            v-model="form.deadline"-->
<!--            type="datetime"-->
<!--            placeholder="选择截止时间"-->
<!--            value-format="yyyy-MM-dd HH:mm:ss"-->
<!--            format="yyyy-MM-dd HH:mm:ss"-->
<!--            style="width: 100%;"-->
<!--        ></el-date-picker>-->
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
      userList: [],
      dialogVisible: false,
      form: {
        id: "",
        title: "",
        description: "",
        status: 0,
        creatorId: "", // 当前登录用户id，实际使用时应自动填充
        assigneeName: "",
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
        assigneeName: [
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
        case 6: return "danger"; // 保留对状态6的处理
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
    // 修改loadTasks方法
    loadTasks() {
      this.$axios.post(this.$httpUrl + "/task/listPageCheck", {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        param: {
          title: this.searchTitle,
          status: this.searchStatus,
          assigneeName: this.searchAssignee,
          excludeStatus: 6 // 确保排除已归档的任务
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          // 额外在前端也过滤一次，确保万无一失
          this.tableData = res.data.filter(task => task.status !== 6);
          this.total = res.total;
        } else {
          this.$message.error("获取任务数据失败");
        }
      });
    },
    openAddDialog() {
      this.form = {
        id: null, // 改为null而不是空字符串
        title: "",
        description: "",
        status: 0,
        creatorId: this.$store?.state?.user?.id || 1, // 自动填充当前用户ID
        assigneeName: "",
        deadline: ""
      };
      this.dialogVisible = true;
    },
    // 处理日期变化
    handleDateChange(val) {
      this.form.deadline = val; // 确保使用选择器格式化后的值
    },

    // 修改openEditDialog方法
    openEditDialog(row) {
      this.form = {
        id: row.id,
        title: row.title,
        description: row.description,
        status: row.status,
        creatorId: row.creatorId,
        assigneeName: row.assigneeName,
        deadline: row.deadline ? this.formatBackendDate(row.deadline) : null
      };
      this.dialogVisible = true;
    },
    // 新增方法：格式化日期
    formatDateTime(date) {
      if (!date) return "";
      const d = new Date(date);
      const pad = num => (num < 10 ? `0${num}` : num);
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
    },
    // 专门处理后端日期格式
    formatBackendDate(date) {
      if (!date) return null;
      // 如果已经是正确格式，直接返回
      if (typeof date === 'string' && date.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) {
        return date;
      }
      // 处理Date对象
      const d = new Date(date);
      const pad = num => (num < 10 ? `0${num}` : num);
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
    },
    save() {
      this.$refs.form.validate(valid => {
        if (!valid) return;

        // 确保creatorId有效
        const creatorId = Number(this.$store?.state?.user?.id) || 1;

        // 构造payload，确保类型正确
        const payload = {
          id: this.form.id ? Number(this.form.id) : null,
          title: this.form.title,
          description: this.form.description,
          status: Number(this.form.status),
          creatorId: creatorId,
          assigneeName: this.form.assigneeName,
          deadline: this.form.deadline || null // 直接使用el-date-picker格式化后的值
        };

        console.log("Sending payload:", JSON.stringify(payload, null, 2));

        const url = this.form.id ? '/task/update' : '/task/save';
        this.$axios.post(this.$httpUrl + url, payload, {
          headers: {
            'Content-Type': 'application/json'
          }
        })
            .then(res => res.data)
            .then(res => {
              if (res.code === 200) {
                this.$message.success(this.form.id ? '修改成功！' : '新增成功！');
                this.dialogVisible = false;
                this.loadTasks();
              } else {
                this.$message.error(res.msg || (this.form.id ? '修改失败！' : '新增失败！'));
              }
            })
            .catch(error => {
              console.error("请求错误详情:", error.response);
              this.$message.error("操作失败: " + (error.response?.data?.message || "请检查数据格式"));
            });
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