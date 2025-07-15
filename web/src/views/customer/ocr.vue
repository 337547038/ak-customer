<template>
  <div><el-button @click="test0">click</el-button></div>
</template>
<script setup lang="ts">

import { createWorker } from 'tesseract.js';
import {onMounted} from "vue";

onMounted(() => {
  //test0()

})

    const test0 = async() => {
      const worker = await createWorker(['eng', 'chi_sim'],1,{
        workerPath:"/js/tesseract/worker.min.js",
        corePath: '/js/tesseract/tesseract-core-simd-lstm.wasm.js', // 本地路径
        //logger: m => console.log(m),
      });
      const ret = await worker.recognize('/3.png');
      console.log(ret.data.text);
      extractCardInfo(ret.data.text);
      extractContactInfo(ret.data.text);
      await worker.terminate();
    }
    const extractCardInfo = (text: string) => {
        // 简单的正则表达式提取信息（实际应用中需要更复杂的逻辑）
        const nameMatch = text.match(/姓名[:：]\s*(\S+)/) || text.match(/^([^\d\n]+)\s*[\n\t]/);
        const titleMatch = text.match(/职位[:：]\s*(\S+)/) || text.match(/(经理|总监|工程师|设计师)/);
        const companyMatch = text.match(/公司[:：]\s*([^\n]+)/);
        const phoneMatch = text.match(/1[3-9]\d{9}/) || text.match(/\d{3,4}[- ]?\d{7,8}/);
        const emailMatch = text.match(/\w+@\w+\.\w+/);

        const cardInfo = {
          name: nameMatch ? nameMatch[1].trim() : '',
          title: titleMatch ? titleMatch[1].trim() : '',
          company: companyMatch ? companyMatch[1].trim() : '',
          phone: phoneMatch ? phoneMatch[0].trim() : '',
          email: emailMatch ? emailMatch[0].trim() : ''
        };
      console.log(cardInfo);
    }
    const extractContactInfo = (text: string) => {
      const info = {};

      // 提取姓名
      const nameMatch = text.match(/^([A-Z][a-z]+(?:\s+[A-Z][a-z]+)+)/);
      info.name = nameMatch ? nameMatch[0] : "";

      // 提取职位
      const titleMatch = text.match(new RegExp(`(?<=${info.name}\\s)([A-Z][a-z]+)`));
      info.title = titleMatch ? titleMatch[0] : "";

      // 提取公司名称
      const companyMatch = text.match(new RegExp(`(?<=${info.name}\\s${info.title}\\s)([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)`));
      info.company = companyMatch ? companyMatch[0] : "";

      // 提取邮箱
      const emailMatch = text.match(/\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b/);
      info.email = emailMatch ? emailMatch[0] : "";

      // 提取电话号码
      const phoneMatch = text.match(/\b(?:\+?\d{1,3}[-. ]?)?\(?\d{3}\)?[-. ]?\d{3}[-. ]?\d{4}\b/);
      info.phone = phoneMatch ? phoneMatch[0] : "";

      // 提取地址（剩余部分）
      const remainingText = text
          .replace(info.name, "")
          .replace(info.title, "")
          .replace(info.company, "")
          .replace(info.email, "")
          .replace(info.phone, "")
          .trim();
      info.address = remainingText || "";
      console.log(info)
    }
    const dddd = () => {
      (async () => {
        console.log("dd")
        const worker = await createWorker('eng');
        console.log("worker")
        console.log(worker);

        const ret = await worker.recognize('https://tesseract.projectnaptha.com/img/eng_bw.png');
        console.log(ret.data.text);
        await worker.terminate();
      })();
    }

</script>
<style scoped lang="scss"></style>
