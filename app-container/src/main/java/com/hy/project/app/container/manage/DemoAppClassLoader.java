package com.hy.project.app.container.manage;

import java.net.URL;
import java.net.URLClassLoader;

public class DemoAppClassLoader extends URLClassLoader {

    public DemoAppClassLoader(URL[] urls) {
        super(urls);
    }

    public DemoAppClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }


}
