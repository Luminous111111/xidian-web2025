<template>
  <div class="comment-list">
    <h3>评论列表 ({{ total }})</h3>

    <!-- 评论列表 -->
    <div v-if="comments.length > 0" class="comments-container">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <!-- 显示用户ID和用户名 -->
          <span class="comment-user">
            用户ID: {{ comment.userId }} | 用户名: {{ getUserName(comment.userId) }}
          </span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </div>
    </div>

    <!-- 无评论状态 -->
    <div v-else class="empty-comment">
      暂无评论，快来发表你的看法吧！
    </div>

    <!-- 分页控件 -->
    <el-pagination
        v-if="total > pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10, 15]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  props: {
    taskId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      comments: [],         // 评论列表
      total: 0,             // 总记录数
      currentPage: 1,       // 当前页码
      pageSize: 3,         // 每页数量
      loading: false,       // 加载状态
      userNameMap: {}       // 缓存用户ID对应的用户名
    };
  },
  created() {
    this.fetchComments(); // 组件创建时加载评论
  },
  methods: {
    /**
     * 获取评论列表
     */

    async fetchComments() {
      this.loading = true;
      console.log('taskId:', this.taskId); // 新增此行，检查是否为 null 或有效数值
      try {
        // 1. 调用后端接口获取评论列表（含用户ID）
        const response = await this.$axios.get(
            `${this.$httpUrl}/task/${this.taskId}/listcomments`,
            {
              params: {
                pageNum: this.currentPage,
                pageSize: this.pageSize
              }
            }
        );

        if (response.data.code === 200) {
          this.comments = response.data.data;
          this.total = response.data.total;

          // 2. 提取所有唯一用户ID
          const userIds = [...new Set(this.comments.map(comment => comment.userId))];

          // 3. 批量查询用户名（减少请求次数）
          await this.fetchUserNames(userIds);
        } else {
          this.$message.error(response.data.msg || '获取评论失败');
        }
      } catch (error) {
        this.$message.error('获取评论列表失败，请重试');
        console.error('评论列表请求失败:', error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * 批量查询用户名称（优化请求次数）
     * @param userIds 用户ID数组
     */
    async fetchUserNames(userIds) {
      if (!this.taskId) { // 新增非空校验
        console.warn('taskId 为空，跳过请求');
        return;
      }

      for (const userId of userIds) {
        // 优先从缓存中获取，避免重复请求
        /*if (this.userNameMap[userId]) continue;*/

        try {
          const userRes = await this.$axios.get(
              `${this.$httpUrl}/user/get-name/${userId}`
          );
          this.$set(this.userNameMap, userId, userRes.data.data || '未知用户');
          console.log('用户名称接口返回:', userRes.data); // 新增此行
          this.userNameMap[userId] = userRes.data.data || '未知用户';
        } catch (error) {
          this.userNameMap[userId] = '未知用户';
          console.error(`用户ID ${userId} 名称查询失败:`, error);
        }
      }
    },

    /**
     * 获取用户名（从缓存中取值）
     * @param userId 用户ID
     * @return 用户名或“未知用户”
     */
    getUserName(userId) {
      console.log('获取用户名:', userId, this.userNameMap);
      return this.userNameMap[userId];
    },

    /**
     * 格式化时间
     * @param timeStr 时间字符串
     * @return 格式化后的时间
     */
    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    /**
     * 分页事件处理
     */
    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.currentPage = 1; // 切换每页数量后重置页码
      this.fetchComments();
    },

    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.fetchComments();
    }
  },
  watch: {
    /**
     * 监听任务ID变化，重新加载评论
     */
    taskId() {
      this.currentPage = 1;
      this.fetchComments();
    }
  }
};
</script>

<style scoped>
/* 样式保持与之前一致，无需修改 */
.comment-list {
  margin-top: 8px;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 5px;
}

.comment-item {
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  font-size: 0.5em;
  justify-content: space-between;
  margin-bottom: 5px;
  color: #666;
}

.comment-content {
  font-size: 0.8em; /* 缩小内容字体 */
  line-height: 0.9; /* 缩小行高 */
}
</style>