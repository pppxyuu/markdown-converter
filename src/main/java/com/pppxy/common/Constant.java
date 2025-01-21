package com.pppxy.common;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static class CSDN {
        public static final String BLOG_CONTENT_ELEMENT = ".blog-content-box";

        public static final String BLOG_TITLE_ELEMENT = ".article-title-box";

        public static final String BLOG_ARTICLE_ELEMENT = ".article_content";

        public static List<String> ignoreTag = new ArrayList<>();

        static {
            ignoreTag.add("svg");
            ignoreTag.add("link");
        }
    }
}
