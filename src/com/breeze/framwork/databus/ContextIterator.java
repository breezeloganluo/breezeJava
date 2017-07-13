/*
 * 这是一个BreezeContext的迭代器，专门迭代BreezeContext里面的数组，支持多个数组情况
 * 例如：a.b[n].c[n].d
 */
package com.breeze.framwork.databus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class ContextIterator {

    public static abstract class ConstContext {

        public static ConstContext getConstContext(String s) {
            return new StringConstContext(s);
        }

        public abstract BreezeContext getContext(BreezeContext c);
        protected ConstContext child;
    }

    static class StringConstContext extends ConstContext {

        StringConstContext(String s) {
            Pattern p = Pattern.compile("([^\\[\\]\\.]+)(\\.?)(.*)");
            Matcher m = p.matcher(s);
            if (m.find()) {
                this.name = m.group(1);
                if (!"".equals(m.group(3))) {
                    if ("".equals(m.group(2))) {
                        this.child = new ArrayConstContext(m.group(3));
                    } else {
                        this.child = new StringConstContext(m.group(3));
                    }
                }
            }
        }
        private String name;

        @Override
        public BreezeContext getContext(BreezeContext c) {
            BreezeContext value = c.getContext(name);
            if (this.child != null) {
                return this.child.getContext(value);
            }
            return value;
        }
    }

    static class ArrayConstContext extends ConstContext {

        private ArrayConstContext(String s) {
            Pattern p = Pattern.compile("^\\[(\\d+)\\]\\.?(.*)");
            Matcher m = p.matcher(s);
            if (m.find()) {
                this.idx = Integer.parseInt(m.group(1));
                if (!"".equals(m.group(2))) {
                    this.child = new StringConstContext(m.group(2));
                }
            }
        }
        private int idx;

        @Override
        public BreezeContext getContext(BreezeContext c) {
            BreezeContext value = c.getContext(idx);
            if (super.child != null) {
                return this.child.getContext(value);
            }
            return value;
        }
    }
    private ConstContext constPath;//静态部分的迭代器
    private ContextIterator child;//下个儿子数组节点
    private BreezeContext _C;//当前自己
    private int idx;//当前数组节点的索引
    private boolean isConst = false;

    /**
     * 默认带path的构造器
     *
     * @param path
     */
    ContextIterator(String path) {
        Pattern p = Pattern.compile("(.*?)\\[n\\](.*)");
        Matcher m = p.matcher(path);
        if (m.find()) {
            this.isConst = false;
            //前面是常量，后面是儿子
            this.constPath = ConstContext.getConstContext(m.group(1));
            if (!"".equals(m.group(2))){
                this.child = new ContextIterator(m.group(2));
            }
        } else {
            //说明全部都是常量来
            this.constPath = ConstContext.getConstContext(path);
            this.isConst = true;
        }
    }

    /**
     * 设置自己迭代的构造器，并且重置索引
     *
     * @param c
     */
    void setContext(BreezeContext c) {
        this.idx =-1;
        this._C = this.constPath.getContext(c);
    }

    /**
     * 判断是否有下一个数据
     *
     * @return
     */
    public boolean next() {
        //先判断是否有儿子，没有儿子的情况要直接处理,常量当作没有儿子处理
        if (this.child == null || this.child.isConst){
            idx++;
            if (idx >= this._C.getArraySize()){
                return false;
            }
            if (this.child != null){
                this.child.setContext(this._C.getContext(this.idx));
            }
            return true;
        }
        //下面是有儿子且儿子不是常量的情况的情况
        boolean childResult = false;
        if (this.idx >=0){
            childResult = this.child.next();
        }
        if (childResult){
            return childResult;
        }
        this.idx++;
        //其他情况增进1
        if (this.idx >= this._C.getArraySize()){
            return false;
        }        
        this.child.setContext(this._C.getContext(this.idx));
        //否则再重新判断一次
        return this.next();
    }

    /**
     * 这个函数用于真正的内部迭代，看看儿子是否真的有值
     *
     * @param last 上次
     * @return 0:有值；1:无值；-1：常量情况，忽略值
     */
    int goNext() {
        //先判断是否有儿子，没有儿子的情况要直接处理
        if (this.child == null ){
            if (this._C.getType() == BreezeContext.TYPE_DATA){
                return -1;
            }
            idx++;
            if (idx >= this._C.getArraySize()){
                return 1;
            }
            return 0;
        }
        //下面是有儿子的情况
        int childResult = 1;
        if (this.idx >=0){
            childResult = this.child.goNext();
        }
        if (childResult == 0){
            return 0;
        }
        //其他情况增进1
        if (this.idx >= this._C.getArraySize()){
            return 1;
        }
        this.idx++;
        this.child.setContext(this._C.getContext(this.idx));
        //如果儿子返回来的是常量，则直接告知有货
        if (childResult == -1){
            return 0;
        }
        //否则再重新判断一次
        return this.goNext();
    }

    /**
     * 获取当前节点
     *
     * @return 返回迭代时的上下文节点
     */
    public BreezeContext getContext() {
        if (this.child != null){
            return this.child.getContext();
        }
        if (this._C.getType() == BreezeContext.TYPE_DATA){
            return this._C;
        }
        return this._C.getContext(this.idx);
    }

    public static void main(String[] args) {
        //测试一下静态迭代器的性能
        BreezeContext root = new BreezeContext();
        String path = "a.b.c.e.f";
        int count = 1000;

        root.setContextByPath(path, new BreezeContext("aaaa"));
        ContextIterator.ConstContext cit = ContextIterator.ConstContext.getConstContext(path);
        if (cit.getContext(root).equals(root.getContextByPath(path))) {
            System.out.println("ready?go!");
        } else {
            return;
        }
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            cit.getContext(root);
        }
        t1 = System.currentTimeMillis() - t1;
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            root.getContextByPath(path);

        }
        t2 = System.currentTimeMillis() - t2;

        System.out.println("t1:" + t1 + "||" + "t2:" + t2 + " ||t1-t2:" + (t1 - t2));
    }
}
