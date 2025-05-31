<template>
  <div>
    <div style="background: #f8f9fa; border-radius: 8px; padding: 20px; margin-bottom: 24px;">
      <h1 style="font-size: 22px; color: #333; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee;">
        {{ task.title }} 任务详情
      </h1>

      <div style="display: grid; gap: 12px;">
        <p style="margin: 0; display: flex; align-items: flex-start;">
          <span style="color: #666; min-width: 60px;">描述：</span>
          <span style="color: #333; line-height: 1.5;">{{ task.description }}</span>
        </p>

        <p style="margin: 0; display: flex; align-items: center;">
          <span style="color: #666; min-width: 60px;">状态：</span>
          <span
              style="display: inline-block;
               padding: 2px 8px;
               border-radius: 4px;
               background: #e8f4ff;
               color: #1890ff;">
        {{ statusLabel(task.status) }}
      </span>
        </p>

        <p style="margin: 0; display: flex;">
          <span style="color: #666; min-width: 60px;">创建人：</span>
          <span style="color: #333;">{{ creatorName }}</span>
        </p>

        <p style="margin: 0; display: flex;">
          <span style="color: #666; min-width: 60px;">负责人：</span>
          <span style="color: #333;">{{ assigneeName }}</span>
        </p>

        <p style="margin: 0; display: flex;">
          <span style="color: #666; min-width: 60px;">截止时间：</span>
          <span style="color: #f56c6c; font-weight: 500;">{{ task.deadline }}</span>
        </p>
      </div>
    </div>

    <!-- 这里可以添加图表组件，例如使用 ECharts -->
    <div id="task-chart" style="width: 600px; height: 0px;"></div>

    <CommentList v-if="taskId" :task-id="taskId" ref="commentList" />
    <CommentForm :task-id="taskId" @comment-added="refreshComments" />

  </div>
</template>

<script>
import echarts from 'echarts';
import CommentForm from "@/components/task/CommentForm.vue";
import CommentList from "@/components/task/CommentList.vue";

export default {

  components: {
    CommentList,
    CommentForm  // 注册组件
  },

  name: "TaskDetail",
  data() {
    return {
      task: null,
      taskId:null,
      creatorName: '',
      assigneeName: '',
      statusOptions: [
        { value: 0, label: "未开始" },
        { value: 1, label: "分析设计" },
        { value: 2, label: "基本搭建" },
        { value: 3, label: "项目开发" },
        { value: 4, label: "测试" },
        { value: 5, label: "完成" }
      ]
    };
  },
  methods: {
    statusLabel(status) {
      const found = this.statusOptions.find(item => item.value === status);
      return found ? found.label : "";
    },
    initChart() {
      // 初始化 ECharts 实例
      const chartDom = document.getElementById('task-chart');
      const myChart = echarts.init(chartDom);
      // 这里可以根据任务数据配置图表选项
      const option = {
        title: {
          text: '任务状态分布'
        },
        tooltip: {},
        xAxis: {
          data: ['未开始', '分析设计', '基本搭建', '项目开发', '测试', '完成']
        },
        yAxis: {},
        series: [
          {
            name: '任务数量',
            type: 'bar',
            data: [1, 1, 1, 1, 1, 1] // 这里需要根据实际任务数据填充
          }
        ]
      };
      myChart.setOption(option);
    },

    /*onCommentAdded() {
      // 这里可以重新获取评论列表数据，更新页面显示
      this.fetchComments();
    },*/

    refreshComments() {
      this.$refs.commentList.fetchComments(); // 刷新评论列表
    },

    // 添加 viewDetail 方法用于跳转到任务详情页
    viewDetail(taskId) {
      console.log("查看任务详情，ID:", taskId); // 调试用
      // 假设你的任务详情页路由路径是 /taskDetail/:id
      this.$router.push({
        path: `/taskDetail/${taskId}`
        // 如果你的路由是 /taskDetail?id=... 的形式，可以使用 query
        // query: { id: taskId }
      });
    }

  },
  mounted() {
    this.taskId = String(this.$route.query.id);
    this.$axios.get(this.$httpUrl + "/task/detail?id=" + this.taskId).then(res => res.data).then(res => {
      if (res.code === 200) {
        this.task = res.data;
        // 获取创建人和被分配人姓名，这里假设后端有对应的接口
        this.$axios.get(this.$httpUrl + "/user/getName?id=" + this.task.creatorId).then(userRes => userRes.data).then(userRes => {
          if (userRes.code === 200) {
            this.creatorName = userRes.data;
          }
        });
        this.$axios.get(this.$httpUrl + "/user/getName?id=" + this.task.assigneeId).then(userRes => userRes.data).then(userRes => {
          if (userRes.code === 200) {
            this.assigneeName = userRes.data;
          }
        });
        this.initChart();
      } else {
        this.$message.error("获取任务详情失败");
      }
    });
  }
};
</script>

<style scoped>
/* 可根据需要自定义样式 */
</style>