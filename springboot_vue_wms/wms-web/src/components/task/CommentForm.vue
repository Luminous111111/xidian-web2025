<template>
  <div class="comment-form">
    <!-- 可选：显示当前用户 -->
    <div v-if="curUser" class="current-user">当前用户：{{ curUser.name }}</div>
    <el-input
        v-model="content"
        type="textarea"
        placeholder="请输入评论内容（最多500字）"
        @keyup.enter.native="submitComment"
    ></el-input>
    <el-button type="primary" @click="submitComment" style="margin-top: 10px;">提交评论</el-button>
    <el-alert v-if="message" :message="message" :type="messageType" :closable="false"></el-alert>
  </div>
</template>

<script>
export default {
  props: {
    taskId: {
      type: String,
      required: true // 从TaskDetail传递的任务ID
    }
  },
  data() {
    return {
      content: '',
      message: '',
      messageType: '',
      curUser: JSON.parse(sessionStorage.getItem("CurUser")) // 从sessionStorage获取当前用户信息
    };
  },
  methods: {
    async submitComment() {
      // 1. 校验用户是否登录
      if (!this.curUser?.id) {
        this.message = '请先登录';
        this.messageType = 'error';
        setTimeout(() => {
          this.$router.push('/login'); // 跳转到登录页
        }, 2000);
        return;
      }

      // 2. 校验评论内容
      if (!this.content.trim()) {
        this.message = '评论内容不能为空';
        this.messageType = 'error';
        return;
      }
      if (this.content.length > 500) {
        this.message = '评论内容不能超过500字';
        this.messageType = 'error';
        return;
      }

      try {
        // 3. 构造请求体（用户ID、任务ID、评论内容）
        const commentData = {
          userId: this.curUser.id,    // 从sessionStorage获取用户ID
          taskId: this.taskId,        // 从prop获取任务ID
          content: this.content.trim()
        };

        // 4. 调用后端评论接口
       /*const response = */await this.$axios.post(
            `${this.$httpUrl}/task/${this.taskId}/comments`,  // 路径包含 taskId
            commentData,
            { headers: { 'Content-Type': 'application/json' } }
        );

        // 5. 提交成功处理
        this.message = '评论提交成功！';
        this.messageType = 'success';
        this.content = ''; // 清空输入框
        this.$emit('comment-added'); // 通知父组件刷新评论列表
      } catch (error) {
        this.message = error.response?.data?.msg || '评论提交失败，请稍后再试';
        this.messageType = 'error';
      }
    }
  }
};
</script>