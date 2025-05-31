<template>
    <div style="text-align: center;background-color: #f1f1f3;height: 100%;padding: 0px;margin: 0px;">
<!--        <h1 style="font-size: 50px;">{{'欢迎你！'+user.name}}</h1>-->
        <el-descriptions  title="个人中心" :column="2" size="40" border style="padding-top: 30px">
            <el-descriptions-item>
                <template slot="label">
                    <i class="el-icon-s-custom"></i>
                    账号
                </template>
                {{user.no}}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    <i class="el-icon-mobile-phone"></i>
                    电话
                </template>
                {{user.phone}}
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    <i class="el-icon-location-outline"></i>
                    性别
                </template>
                <el-tag
                        :type="user.sex === '1' ? 'primary' : 'danger'"
                        disable-transitions><i :class="user.sex==1?'el-icon-male':'el-icon-female'"></i>{{user.sex==1?"男":"女"}}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    <i class="el-icon-tickets"></i>
                    角色
                </template>
                <el-tag
                        type="success"
                        disable-transitions>{{user.roleId==0?"超级管理员":(user.roleId==1?"管理员":"用户")}}</el-tag>

            </el-descriptions-item>
        </el-descriptions>

        <DateUtils></DateUtils>
        <!-- 任务通知列表 -->
        <div style="width: 90%; margin: 30px auto 0; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px #eee; padding: 20px;">
            <div style="font-size: 18px; font-weight: bold; margin-bottom: 10px; text-align: left;">任务通知</div>
            <el-table :data="notifyList" style="width: 100%;" border>
                <el-table-column prop="title" label="标题" width="180"/>
                <el-table-column prop="content" label="内容"/>
                <el-table-column prop="time" label="时间" width="160"/>
                <el-table-column label="状态" width="80">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.read ? 'info' : 'danger'">{{ scope.row.read ? '已读' : '未读' }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                style="margin-top: 10px; text-align: right;"
                @current-change="handlePageChange"
                :current-page="pageNum"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    import DateUtils from "./DateUtils";
    export default {
        name: "Home",
        components: {DateUtils},
        data() {
            return {
                user:{},
                notifyList: [],
                pageNum: 1,
                pageSize: 10,
                total: 0
            }
        },
        methods:{
            init(){
                this.user = JSON.parse(sessionStorage.getItem('CurUser'))
            },
            fetchNotifyList() {
                this.$axios.get('http://localhost:8090/notify/list', {
                    params: {
                        userId: this.user.id,
                        pageNum: this.pageNum,
                        pageSize: this.pageSize
                    }
                }).then(res => {
                    if(res.data.code === 200) {
                        const records = res.data.data.records || [];
                        this.notifyList = records.map(item => ({
                            ...item,
                            time: item.createTime,
                            read: item.status === 1
                        }));
                        this.total = res.data.data.total || 0;
                        // 检查是否有未读消息，有则设为已读
                        const unreadIds = this.notifyList.filter(item => !item.read).map(item => item.id);
                        if (unreadIds.length > 0) {
                            this.$axios.post('http://localhost:8090/notify/read', {
                                userId: this.user.id,
                                notifyIds: unreadIds
                            }).then(() => {
                                // 通知Header刷新信箱
                                this.$root.$emit('refreshNotify');
                                // 再次刷新本页，确保状态同步
                                this.$axios.get('http://localhost:8090/notify/list', {
                                    params: {
                                        userId: this.user.id,
                                        pageNum: this.pageNum,
                                        pageSize: this.pageSize
                                    }
                                }).then(res2 => {
                                    if(res2.data.code === 200) {
                                        const records2 = res2.data.data.records || [];
                                        this.notifyList = records2.map(item => ({
                                            ...item,
                                            time: item.createTime,
                                            read: item.status === 1
                                        }));
                                        this.total = res2.data.data.total || 0;
                                    }
                                });
                            });
                        }
                    } else {
                        this.$message.error(res.data.msg || "获取通知失败");
                    }
                }).catch(() => {
                    this.$message.error("无法连接通知服务，请检查后端8090端口");
                });
            },
            handlePageChange(page) {
                this.pageNum = page;
                this.fetchNotifyList();
            },
            markAllRead() {
                const unreadIds = this.notifyList.filter(item => !item.read).map(item => item.id);
                if (unreadIds.length > 0) {
                    this.$axios.post('http://localhost:8090/notify/read', {
                        userId: this.user.id,
                        notifyIds: unreadIds
                    }).then(() => {
                        // 刷新信箱
                        this.$root.$emit('refreshNotify');
                        // 刷新本页
                        this.fetchNotifyList();
                    });
                }
            },
            onNotifyRead() {
                this.fetchNotifyList();
            }
        },
        created(){
            this.init();
            this.fetchNotifyList();
            // 监听Header的已读事件
            this.$root.$on('refreshNotify', this.fetchNotifyList);
        },
        beforeDestroy() {
            this.$root.$off('refreshNotify', this.fetchNotifyList);
        }
    }
</script>

<style scoped>
    .el-descriptions{
        width:90%;

        margin: 0 auto;
        text-align: center;
    }
</style>