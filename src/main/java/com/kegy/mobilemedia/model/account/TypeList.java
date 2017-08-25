package com.kegy.mobilemedia.model.account;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */


public class TypeList implements Serializable {
    @Expose
    private String ret;
    @Expose
    private String ret_msg;
    @Expose
    @SerializedName("type_list")
    private List<TypeChildren> type_list;

    public List<TypeChildren> getType_list() {
        return type_list;
    }

    public void setType_list(List<TypeChildren> type_list) {
        this.type_list = type_list;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public static class TypeChildren implements Serializable {
        @Expose
        @SerializedName("children")
        private List<TypeChildren> children;
        @Expose
        private String desc;//新版废弃
        @Expose
        private int id;
        @Expose
        private int is_hide;//新版废弃
        @Expose
        private String name;

        @Expose
        @SerializedName("program_property")
        private ProgramProperty program_property;

        //新版vcontrol=1返回，vcontrol=0不返回，但labelposition也不会包含在其中；vcontrol缺省时返回，包含labelPosition
        @Expose
        private String style;
        @Expose
        private TypeChildren parrentTypeChildren;

        //新版vcontrol=1或vcontrol=0返回labelPosition,vcontrol缺省时在style中返回labelPosition；
        //虽然服务器说返回的是数值型，万一那个现场的设置成中文呢...，个人觉得把类型设置成String保险一点
        @Expose
        @SerializedName("labelPosition")
        private String labelPosition;

        public TypeChildren(){

        }
        public List<TypeChildren> getChildren() {
            return children;
        }
        public void setChildren(List<TypeChildren> children) {
            this.children = children;
        }
        public String getDesc() {
            return desc;
        }
        public void setDesc(String desc) {
            this.desc = desc;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getIs_hide() {
            return is_hide;
        }
        public void setIs_hide(int is_hide) {
            this.is_hide = is_hide;
        }
        public TypeChildren getParrentTypeChildren() {
            return parrentTypeChildren;
        }
        public void setParrentTypeChildren(TypeChildren parrentTypeChildren) {
            this.parrentTypeChildren = parrentTypeChildren;
        }


        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public ProgramProperty getProgram_property() {
            return program_property;
        }
        public void setProgram_property(ProgramProperty program_property) {
            this.program_property = program_property;
        }

        /**
         *  新版vcontrol=1返回，vcontrol=0不返回，但labelposition也不会包含在其中；</br>
         *  vcontrol缺省时返回，包含labelPosition
         * */
        public String getStyle() {
            return style;
        }
        public void setStyle(String style) {
            this.style = style;
        }

        /**
         * 获取栏目位，没有栏目位时取值为0</br>
         * 需要做新旧版的兼容处理</br>
         * 新版vcontrol=1或vcontrol=0返回labelPosition,vcontrol缺省时在style中返回labelPosition</br>
         * */
        public int getLabelPosition()
        {
            //优先考虑labelPosition是不是null的
            if(!TextUtils.isEmpty(labelPosition)){
//		        Log.i("TypeListObject", "the labelPosition is not style's element-->"+labelPosition);
                try{
                    int position=Integer.parseInt(labelPosition);
                    return position;
                }catch(Exception e){
                    //异常了 估计返回的不是数值型的 返回0处理
                    e.printStackTrace();
                    return 0;
                }
            }
            //兼容旧版本处理
            if(TextUtils.isEmpty(style))
                return 0;
            try {
                JSONObject jsonObject = new JSONObject(style);
                int label = 0;
                if(jsonObject.has("labelPosition"))
                    label = jsonObject.getInt("labelPosition");
//			    Log.i("TypeListObject", "the labelPosition is style's element-->"+label);
                return label;
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public TypeChildren getChildrenByLabelId(TypeChildren item, int currentId) {
            // TODO Auto-generated method stub
            TypeChildren dest = null;
            if(item == null)
                return null;
            else if(item.getId() == currentId)
                dest = item;
            else if(item.getChildren()!=null && item.getChildren().size() > 0){
                for(TypeChildren children : item.getChildren()){
                    if(children.getId() == currentId){
                        dest = item;
                        break;
                    }else{
                        if(children.getChildren()!=null && children.getChildren().size() > 0){
                            if(getChildrenByLabelId(children, currentId)!=null){
                                dest = getChildrenByLabelId(children, currentId);
                                break;
                            }
                        }
                    }
                }
            }else
                return null;
            return dest;
        }

        /**
         * 从节目属性program_property获取栏目节点的媒资内容类型</br>
         * program_property为null是返回0处理
         *
         * */
        public int getContentType(){
            if(program_property!=null)
                return program_property.content_type;
            return 0;
        }

        /**
         * 从节目属性program_property获取搜索值列表
         * program_property为空时返回null
         * */
        public FilterInfo getFilterInfo() {
            if(program_property!=null)
                return program_property.getFilter_info();
            return null;
        }

        /**
         * 节目属性对像</br>
         * 新版本保留了属性：content_type</br>
         * 新版本增加属性：filter_info->2017-04-24
         * */
        public static class ProgramProperty implements Serializable{
            @Expose
            private String type;//新版废弃
            @Expose
            private String tab;//新版废弃
            @Expose
            private String provider_id;//新版废弃

            /**
             * 该栏目节点的媒资内容类型，取值详见媒资类型定义</br>
             * 数值型
             * */
            @Expose
            private int content_type;

            /**
             * 搜索值列表
             * */
            private FilterInfo filter_info;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getTab() {
                return tab;
            }

            public void setTab(String tab) {
                this.tab = tab;
            }

            public String getProvider_id() {
                return provider_id;
            }

            public void setProvider_id(String provider_id) {
                this.provider_id = provider_id;
            }

            public int getContent_type() {
                return content_type;
            }

            public void setContent_type(int content_type) {
                this.content_type = content_type;
            }

            /**
             * 搜索值列表
             * */
            public FilterInfo getFilter_info() {
                return filter_info;
            }

            public void setFilter_info(FilterInfo filter_info) {
                this.filter_info = filter_info;
            }


        }

        public static class FilterInfo implements Serializable{

            private FilterEntity type;

            private List<FilterEntity> items;

            /**
             * 搜索类型
             * id，数值型，搜索类型id
             * name，字符串型，搜索类型名
             * */
            public FilterEntity getType() {
                return type;
            }

            public void setType(FilterEntity type) {
                this.type = type;
            }

            /**搜索对象列表
             * id，数值型，搜索值id
             * name，字符串型，搜索值名称
             * */
            public List<FilterEntity> getItems() {
                return items;
            }

            public void setItems(List<FilterEntity> items) {
                this.items = items;
            }


        }

        public static class FilterEntity implements Serializable{
            private long id;

            private  String name;

            /**
             * 在type搜索类型中表示搜索类型id
             * <br>在items搜索对象列表中表示搜索值id
             * */
            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            /**
             * 在type搜索类型中表示搜索类型名
             * <br>在items搜索对象列表中表示搜索值名称
             * */
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }


        }

    }
}
