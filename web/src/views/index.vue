<template>
  <div>
    <div class="item-box">
      <h3>待办事项</h3>
      <div class="flex">
        <div class="item" v-if="hasChild">
          <i class="icon-contract2" style="color: #ee614e"></i>
          <div class="text">
            <p class="num">{{ todo.contract }}</p>
            <p>待审批合同</p>
          </div>
        </div>
        <div class="item" v-if="hasChild">
          <i class="icon-payment" style="color:#f6bc33;"></i>
          <div class="text">
            <p class="num">{{ todo.payment }}</p>
            <p>待审批回款</p>
          </div>
        </div>
        <div class="item">
          <i class="icon-follow-record" style="color:#cd94ff;"></i>
          <div class="text">
            <p class="num">{{ todo.follow }}</p>
            <p>待跟进客户</p>
          </div>
        </div>
      </div>
    </div>
    <div class="flex">
      <div class="border-box">
        <h3>待跟进客户
          <el-tooltip
              class="box-item"
              effect="dark"
              content="设置了下次跟进时间的客户"
              placement="top-start"
          >
            <el-icon>
              <InfoFilled/>
            </el-icon>
          </el-tooltip>
        </h3>
        <div class="item">
          <ul>
            <li>广州有限公司<span>2019-12-12</span></li>
            <li>广州有限公司<span>2019-12-12</span></li>
          </ul>
        </div>
      </div>
      <div class="border-box">
        <h3>超30天无跟进记录的客户
          <el-tooltip
              class="box-item"
              effect="dark"
              content="长时间不跟进的客户将流入公海"
              placement="top-start"
          >
            <el-icon>
              <InfoFilled/>
            </el-icon>
          </el-tooltip>
        </h3>
        <div class="item">
          <ul>
            <li>广州有限公司<span>2019-12-12</span></li>
            <li>广州有限公司<span>2019-12-12</span></li>
          </ul>
        </div>
      </div>
      <div class="border-box">
        <h3>客户生日提醒</h3>
        <div class="item">
          <ul>
            <li>广州有限公司　张总　<span>8月16</span></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<route>
{meta:{permissions:"none"}}
</route>
<script lang="ts" setup>

  import {InfoFilled} from "@element-plus/icons-vue";
  import {useLayoutStore} from "@/store/layout";
  import {computed, ref, onMounted} from "vue";
  import {getRequest} from "@/api";

  const layerStore = useLayoutStore()
  const hasChild = computed(() => {
    return layerStore.userInfo?.hasChild
  })
  const todo = ref({})
  const getData = () => {
    getRequest("analysisSummary", {})
        .then(res => {
          todo.value = res.data || {}
        })
  }
  onMounted(() => {
    getData()
  })

</script>
<style scoped lang="scss">
.border-box {
  border: 1px solid #eee;
  border-radius: 4px;
  background: #fff;
  width: 32%;
  margin-right: 10px;

  h3 {
    border-bottom: 1px solid #eee;
    padding: 15px 20px;
  }

  .item {
    padding: 20px;

    li {
      height: 30px;
      line-height: 30px;
      display: flex;
      justify-content: space-around;

      span {
        flex: 2;
        text-align: right
      }
    }
  }
}

.item-box {

  margin-bottom: 15px;

  h3 {
    font-size: 16px;
    font-weight: normal;
    padding: 5px 10px;
  }

  .item {
    background: #fff;
    border-radius: 5px;
    width: 350px;
    height: 180px;
    margin-right: 15px;
    display: flex;
    align-items: center;

    i {
      display: block;
      font-size: 80px;
      margin: 0 20px;
    }

    .num {
      font-size: 36px;
    }
  }
}

.flex {
  display: flex;

}
</style>