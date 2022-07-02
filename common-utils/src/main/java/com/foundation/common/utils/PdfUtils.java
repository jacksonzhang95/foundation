package com.foundation.common.utils;

import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author : jacksonz
 * @date : 2022/4/29 9:52
 */
public class PdfUtils {

    public static ByteArrayOutputStream htmlToPdf(String htmlStr) throws DocumentException {
        // fix html format
        String html = fixHtmlFormat(htmlStr);

        // 输出文件流
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver font = renderer.getFontResolver();
        // 找Resources下的字体文件
        try {
            ClassPathResource simsunResource = new ClassPathResource("simsun.ttc");
            ClassPathResource arialuniResource = new ClassPathResource("arialuni.ttf");
            ClassPathResource notoEmojiResource = new ClassPathResource("NotoEmoji-Regular.ttf");
            if (simsunResource.exists()) {
                if ("linux".equals(getCurrentOperatingSystem())) {
                    font.addFont(simsunResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } else {
                    font.addFont(simsunResource.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                }
            }
            if (arialuniResource.exists()) {
                if ("linux".equals(getCurrentOperatingSystem())) {
                    font.addFont(arialuniResource.getPath(), BaseFont.IDENTITY_H, true);
                }
            }
            font.addFont(notoEmojiResource.getPath(), BaseFont.IDENTITY_H, true);
        } catch (Exception e) {
            // TODO
        }

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(os);
        renderer.finishPDF();
        return os;
    }

    private static String fixHtmlFormat(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        // 移除特定标签，然后补全
        content = content.replaceAll("<html>", "")
                .replaceAll("</html>", "");

        String header = "<html><head><style>body{font-family:SimSun;font-size:14px;}</style></head><body>";
        String ending = "</body></html>";

        String html = header + content + ending;

        // 去除一些无法解析的特殊字符
        html = html.replaceAll("\"", "'");
        return html;
    }

    private static String getCurrentOperatingSystem() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        String text = "";
        ByteArrayOutputStream outputStream = PdfUtils.htmlToPdf(text);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/新建文件夹/test.pdf"));
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.flush();

        outputStream.close();
        fileOutputStream.close();
    }
}
