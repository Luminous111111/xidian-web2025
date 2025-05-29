<template>
  <div class="task-statistics">
    <h2>任务统计信息</h2>

    <p>总任务数：{{ totalTasks }}</p>

    <div ref="ganttChart" style="width: 100%; height: 500px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "TaskStatistics",
  data() {
    return {
      totalTasks: 0,
      statusData: []
    };
  },
  mounted() {
        const chartData = JSON.parse(this.$route.query.chartData);
        this.totalTasks = chartData.values.reduce((a, b) => a + b, 0);
        this.renderChart(chartData.labels, chartData.values);
  },
  methods: {
    renderChart(labels, data) {
      const chartDom = this.$refs.ganttChart;
      const myChart = echarts.init(chartDom);

      const option = {
        title: { text: '任务阶段分布' },
        tooltip: {},
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value' },
        series: [{
          name: '任务数量',
          type: 'bar',
          data: data
        }]
      };

      myChart.setOption(option);
    }
  }
};
</script>

<style scoped>
.task-statistics {
  padding: 20px;
}
</style>
