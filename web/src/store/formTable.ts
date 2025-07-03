import { defineStore } from 'pinia'

export const useFormTable = defineStore('formTable', {
  state: () => {
    return {
      columnsCheck:
        JSON.parse(<string>window.localStorage.getItem('tableColumns')) || {} // 表格勾选的列
    }
  },
  actions: {
    setColumnsCheck(path: string, data: string[]) {
      this.columnsCheck[path] = data
      window.localStorage.setItem(
        'tableColumns',
        JSON.stringify(this.columnsCheck)
      )
    },
    getColumnsCheck(path: string) {
      try {
        return this.columnsCheck[path] || []
      } catch (e) {
        return []
      }
    }
  }
})
