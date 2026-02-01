import {Button, Form, Field, Cell,CellGroup, Popup, Picker, DatePicker,List,Tag,Empty,
    Sticky,PullRefresh,NavBar,Icon,Tabbar, TabbarItem,BackTop,Loading,SwipeCell,Switch,
    Cascader, Checkbox, CheckboxGroup, Search,ActionBar,Collapse,CollapseItem,Tab,Tabs,
    Uploader,TimePicker, PickerGroup,NoticeBar  } from 'vant'
import type {App} from 'vue';
import 'vant/lib/index.css'
export default (app: App) => {
    app
        .use(Button)
        .use(Form)
        .use(Field)
        .use(Cell)
        .use(CellGroup)
        .use(Popup)
        .use(Picker)
        .use(DatePicker)
        .use(List)
        .use(Tag)
        .use(Empty)
        .use(Sticky)
        .use(PullRefresh)
        .use(NavBar)
        .use(Icon)
        .use(Tabbar)
        .use(TabbarItem)
        .use(BackTop)
        .use(Loading)
        .use(SwipeCell)
        .use(Switch)
        .use(Cascader)
        .use(Checkbox)
        .use(CheckboxGroup)
        .use(Search)
        .use(ActionBar)
        .use(Collapse)
        .use(CollapseItem)
        .use(Tab)
        .use(Tabs)
        .use(Uploader)
        .use(TimePicker)
        .use(PickerGroup)
        .use(NoticeBar)
}
