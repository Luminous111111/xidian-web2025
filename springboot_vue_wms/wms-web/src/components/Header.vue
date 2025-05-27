<template>
    <div style="display: flex;line-height: 60px;">
        <div style="margin-top: 8px;">
            <i :class="icon" style="font-size: 20px;cursor: pointer;" @click="collapse"></i>
        </div>
        <div style="flex: 1;text-align: center;font-size: 20px;">
            <span>欢迎来到任务管理系统</span>
        </div>
        <!-- 用户区：信箱+用户名+下拉 -->
        <div style="display: flex; align-items: center; position: relative;">
            <!-- 信箱图标 -->
            <div
                ref="mailIcon"
                class="mail-icon"
                :class="{ shake: isShaking }"
                @mouseenter="handleMailEnter"
                @mouseleave="handleMailLeave"
                @click="goHome"
                style="display: inline-block; cursor: pointer; position: relative; margin-right: 10px;"
            >
                <i class="el-icon-message" style="font-size: 22px;"></i>
                <span v-if="unreadCount > 0" class="mail-dot"></span>
            </div>
            <el-dropdown>
                <span>{{user.name}}</span><i class="el-icon-arrow-down" style="margin-left: 5px;"></i>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
                    <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <!-- 浮动消息栏（全局fixed定位） -->
        <div
            v-if="showNotify"
            class="notify-popover"
            :style="notifyPopoverStyle"
            @mouseenter="cancelHide"
            @mouseleave="handleMailLeave"
        >
            <div v-if="notifyList.length === 0" style="padding: 10px 20px; color: #888;">暂无新消息</div>
            <div v-for="item in notifyList" :key="item.id" class="notify-item" @click="goHome">
                <span style="font-weight: bold;">{{ item.title }}</span>
                <div style="font-size: 12px; color: #888;">{{ item.time }}</div>
                <div style="font-size: 13px;">{{ item.content }}</div>
            </div>
            <div v-if="notifyList.length > 0" class="notify-footer" @click="goHome">
                查看全部消息
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Header",
    data(){
        return {
            user : JSON.parse(sessionStorage.getItem('CurUser')),
            unreadCount: 0,
            notifyList: [],
            showNotify: false,
            isShaking: false,
            shakeTimer: null,
            pollTimer: null,
            notifyPopoverStyle: {
                display: 'none'
            },
            hideTimer: null
        }
    },
    props:{
        icon:String
    },
    methods:{
        toUser(){
            this.$store.commit('setActiveMenu', '/Home');
            this.$router.push("/Home");
        },
        logout(){
            this.$confirm('您确定要退出登录吗?', '提示', {
                confirmButtonText: '确定',
                type: 'warning',
                center: true,
            })
                .then(() => {
                    this.$message({
                        type:'success',
                        message:'退出登录成功'
                    })
                    this.$router.push("/")
                    sessionStorage.clear()
                })
                .catch(() => {
                    this.$message({
                        type:'info',
                        message:'已取消退出登录'
                    })
                })
        },
        collapse(){
            this.$emit('doCollapse')
        },
        goHome() {
            // 获取第一页未读消息id
            const unreadIds = this.notifyList.filter(item => !item.read).map(item => item.id);
            if (unreadIds.length > 0) {
                // 批量设为已读
                this.$axios.post('http://localhost:8090/notify/read', {
                    userId: this.user.id,
                    notifyIds: unreadIds
                }).then(() => {
                    // 立即刷新信箱消息
                    this.fetchNotify();
                    // 通知首页刷新
                    this.$root.$emit('refreshNotify');
                    this.$router.push("/Home");
                    this.showNotify = false;
                });
            } else {
                this.$router.push("/Home");
                this.showNotify = false;
            }
        },
        fetchNotify() {
            this.$axios.get('http://localhost:8090/notify/unreadCount', {
                params: { userId: this.user.id }
            }).then(res => {
                if(res.data.code === 200) {
                    this.unreadCount = res.data.data;
                    if(this.unreadCount > 0) this.triggerShake();
                }
            });
            this.$axios.get('http://localhost:8090/notify/list', {
                params: { userId: this.user.id, pageNum: 1, pageSize: 3 }
            }).then(res => {
                if(res.data.code === 200) {
                    const records = res.data.data.records || [];
                    // 只保留未读消息
                    this.notifyList = records
                        .map(item => ({
                            ...item,
                            time: item.createTime,
                            read: item.status === 1
                        }))
                        .filter(item => !item.read);
                }
            });
        },
        triggerShake() {
            if(this.isShaking) return;
            this.isShaking = true;
            setTimeout(() => { this.isShaking = false; }, 800);
        },
        handleMailEnter() {
            if (this.hideTimer) {
                clearTimeout(this.hideTimer);
                this.hideTimer = null;
            }
            this.showNotify = true;
            this.$nextTick(() => {
                const icon = this.$refs.mailIcon;
                if (icon) {
                    const rect = icon.getBoundingClientRect();
                    this.notifyPopoverStyle = {
                        position: 'fixed',
                        left: rect.left + rect.width / 2 - 130 + 'px',
                        top: rect.bottom + 8 + 'px',
                        width: '260px',
                        background: '#fff',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                        borderRadius: '6px',
                        zIndex: 999,
                        padding: 0
                    };
                }
            });
        },
        handleMailLeave() {
            this.hideTimer = setTimeout(() => {
                this.showNotify = false;
                this.notifyPopoverStyle = { display: 'none' };
            }, 200);
        },
        cancelHide() {
            if (this.hideTimer) {
                clearTimeout(this.hideTimer);
                this.hideTimer = null;
            }
        },
    },
    mounted() {
        this.fetchNotify();
        this.pollTimer = setInterval(this.fetchNotify, 10000);
    },
    beforeDestroy() {
        if(this.pollTimer) clearInterval(this.pollTimer);
    }
}
</script>

<style scoped>
.mail-dot {
    position: absolute;
    top: 2px;
    right: 2px;
    width: 8px;
    height: 8px;
    background: red;
    border-radius: 50%;
    display: inline-block;
}
.mail-icon.shake {
    animation: shake 0.8s;
}
@keyframes shake {
    0% { transform: rotate(0deg);}
    20% { transform: rotate(-15deg);}
    40% { transform: rotate(15deg);}
    60% { transform: rotate(-10deg);}
    80% { transform: rotate(10deg);}
    100% { transform: rotate(0deg);}
}
.notify-popover {
    /* 样式由JS动态控制定位和宽度 */
}
.notify-item {
    padding: 10px 20px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
}
.notify-item:last-child {
    border-bottom: none;
}
.notify-footer {
    text-align: center;
    padding: 8px 0;
    color: #409EFF;
    cursor: pointer;
    font-size: 13px;
    border-top: 1px solid #f0f0f0;
}
</style>