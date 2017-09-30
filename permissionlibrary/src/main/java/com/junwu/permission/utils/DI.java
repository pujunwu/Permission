package com.junwu.permission.utils;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2017/9/30 10:47
 * ===============================
 */
public class DI extends Exception {

    public int line() {
        StackTraceElement[] trace = getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[0].getLineNumber();
    }

    public int line(int off) {
        StackTraceElement[] trace = getStackTrace();
        if (trace == null || trace.length == 0)
            return -1; //
        return trace[off].getLineNumber();
    }

    public String fun() {
        StackTraceElement[] trace = getStackTrace();
        if (trace == null)
            return ""; //
        return trace[0].getMethodName();
    }

    public String fun(int off) {
        StackTraceElement[] trace = getStackTrace();
        if (trace == null)
            return ""; //
        return trace[off].getMethodName();
    }

    public String funLog(int ser) {
        StackTraceElement[] trace = getStackTrace();
        if (trace == null)
            return ""; //
        if (ser > trace.length) {
            ser = trace.length;
        }
        StringBuilder sb = new StringBuilder();
        StackTraceElement element;
        for (int i = ser - 1; i >= 0; i--) {
            sb.append("\n");
            element = trace[i];
            sb.append(element.getClassName());
            sb.append(".");
            sb.append(element.getMethodName());
            sb.append("()");
            sb.append(":(");
            sb.append(element.getFileName());
            sb.append(":");
            sb.append(element.getLineNumber());
            sb.append(")");
        }
        return sb.toString();
    }

}
