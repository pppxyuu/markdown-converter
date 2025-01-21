package com.pppxy;

import com.pppxy.common.Constant;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class CSDNToMarkdown {

    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Usage: java -jar CSDNToMarkdown.jar <CSDN_BLOG_URL>");
            return;
        }
        String url = args[0];

        try {
            String markdown = convertCSDNToMarkdown(url);
            System.out.println("CSDN convert to Markdown success.");

            //输出结果
            String fileName = "output";
            try (FileWriter writer = new FileWriter(fileName + ".md")){
                writer.write(markdown);
                System.out.println("Markdown content written to " + fileName + ".md");
            } catch (IOException e){
                System.out.println("Error writing to file: " + e.getMessage());
            }
        } catch (IOException e){
            System.out.println("Error fetching or parsing the URL: " + e.getMessage());
        }
    }

    public static String convertCSDNToMarkdown(String url) throws IOException {
        // Fetch the HTML content
        Document doc = Jsoup.connect(url).get();

        // Extract the page title
        String title = doc.title();

        //提取中间的内容页面
        Element contentElement = doc.selectFirst(Constant.CSDN.BLOG_CONTENT_ELEMENT);
        if (contentElement == null){
            throw new IOException("Blog content not found.");
        }

        StringBuilder markdown = new StringBuilder();
        convertElementToMarkdown(contentElement, title, markdown);
        return markdown.toString();
    }

    private static void convertElementToMarkdown(Element element, String title, StringBuilder markdown) {
        Element titleElement = element.selectFirst(Constant.CSDN.BLOG_TITLE_ELEMENT);
        if (Objects.nonNull(titleElement) && StringUtils.isNoneBlank(titleElement.text())){
            title = titleElement.text();
        }
        markdown.append("# ").append(title).append("\n\n");

        Element contentElement = element.selectFirst(Constant.CSDN.BLOG_ARTICLE_ELEMENT);
        Element contentViews = contentElement.children().stream().filter(s -> StringUtils.equals(s.id(), "content_views")).findFirst().get();

        convertContentRecursion(contentViews, markdown, "");

    }

    //循环迭代解析html元素
    private static void convertContentRecursion(Element element, StringBuilder markdown, String parentTag) {
        if (Constant.CSDN.ignoreTag.contains(element.tagName())){
            return;
        }
        if (element.childrenSize()==0){
            convertContent(element, markdown, parentTag);
        } else if (isContentBlockIgnoreInner(element.tagName())){
            convertContent(element, markdown, element.tagName());
        } else {
            for (Node childNode : element.childNodes()){
                if (childNode instanceof TextNode){
                    convertContent(((TextNode) childNode).text(), markdown, element.tagName());
                } else {
                    convertContentRecursion((Element) childNode, markdown, element.tagName());
                }
            }
        }
    }

    private static void convertContent(Element element, StringBuilder markdown, String parentTag){
        if (isBlankContentToIgnore(element.text(), element.tagName())){
            return;
        }
        String text = element.text();
        switch (element.tagName()) {
            case "link":
            case "svg":
                break;
            case "br":
            case "br/":
                markdown.append("\n\n");
                break;
            case "p":
                markdown.append(text).append("\n\n");
                break;
            case "h1":
                markdown.append("# ").append(text).append("\n\n");
                break;
            case "h2":
                markdown.append("## ").append(text).append("\n\n");
                break;
            case "h3":
                markdown.append("### ").append(text).append("\n\n");
                break;
            case "h4":
                markdown.append("#### ").append(text).append("\n\n");
                break;
            case "h5":
                markdown.append("##### ").append(text).append("\n\n");
                break;
            case "h6":
                markdown.append("###### ").append(text).append("\n\n");
                break;
            case "ul":
                for (Element li : element.select("li")){
                    markdown.append("- ").append(li.text()).append("\n");
                }
                markdown.append("\n\n");
                break;
            case "li":
                markdown.append("* ").append(text).append("\n\n");
                break;
            case "ol":
                int index = 1;
                for (Element li : element.select("li")){
                    markdown.append(index).append(". ").append(li.text()).append("\n");
                }
                markdown.append("\n\n");
                break;
            case "img":
                String src = element.attr("src");
                String alt = element.attr("alt");
                markdown.append("![")
                        .append(alt != null ? alt : "")
                        .append("](")
                        .append(src)
                        .append(")\n\n");
                break;
            case "code":
                markdown.append("```\n").append(text).append("\n```").append("\n\n");
                break;
            case "strong":
                markdown.append("**").append(text).append("**").append("\n\n");
                break;
            case "hr":
            case "hr/":
                markdown.append("------").append("\n\n");
                break;
            default:
                if (StringUtils.isBlank(parentTag)){
                    markdown.append(text);
                } else {
                    convertContent(element.text(), markdown, parentTag);
                }
                break;
        }
    }

    private static void convertContent(String text, StringBuilder markdown, String parentTag){
        if (isBlankContentToIgnore(text, parentTag)){
            return;
        }
        switch (parentTag) {
            case "link":
            case "svg":
                break;
            case "br":
            case "br/":
                markdown.append("\n\n");
                break;
            case "p":
                markdown.append(text).append("\n\n");
                break;
            case "h1":
                markdown.append("# ").append(text).append("\n\n");
                break;
            case "h2":
                markdown.append("## ").append(text).append("\n\n");
                break;
            case "h3":
                markdown.append("### ").append(text).append("\n\n");
                break;
            case "h4":
                markdown.append("#### ").append(text).append("\n\n");
                break;
            case "h5":
                markdown.append("##### ").append(text).append("\n\n");
                break;
            case "h6":
                markdown.append("###### ").append(text).append("\n\n");
                break;
            case "li":
                markdown.append("* ").append(text).append("\n\n");
                break;
            case "code":
                markdown.append("```\n").append(text).append("\n```").append("\n\n");
                break;
            case "strong":
                markdown.append("**").append(text).append("**").append("\n\n");
                break;
            case "hr":
            case "hr/":
                markdown.append("------").append("\n\n");
                break;
            default:
                markdown.append(text);
                break;
        }
    }

    private static boolean isBlankContentToIgnore(String text, String tag){
        return StringUtils.isBlank(text)
                && !StringUtils.equals(tag, "img")
                && !StringUtils.equals(tag, "hr");
    }

    private static boolean isContentBlockIgnoreInner(String tag){
        return StringUtils.equals(tag, "code")
                || StringUtils.equals(tag, "ul")
                || StringUtils.equals(tag, "ol")
                || StringUtils.equals(tag, "img");
    }
}
