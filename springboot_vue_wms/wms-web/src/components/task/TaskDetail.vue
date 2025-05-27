<template>
  <div>
    <h1>{{ task.title }} 任务详情</h1>
    <p>描述: {{ task.description }}</p>
    <p>状态: {{ statusLabel(task.status) }}</p>
    <p>创建人: {{ creatorName }}</p>
    <p>被分配人: {{ assigneeName }}</p>
    <p>截止时间: {{ task.deadline }}</p>

    <!-- 这里可以添加图表组件，例如使用 ECharts -->
    <div id="task-chart" style="width: 600px; height: 400px;"></div>
  </div>
</template>

<script>
import echarts from 'echarts';

export default {
  name: "TaskDetail",
  data() {
    return {
      task: {},
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
    }
  },
  mounted() {
    const taskId = this.$route.query.id;
    this.$axios.get(this.$httpUrl + "/task/detail?id=" + taskId).then(res => res.data).then(res => {
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