package com.quick.common.utils.flie;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 基于aspose-words-15.8.0-jdk16.jar 实现文件转换PDF文件
 *
 * @author Liujinxin
 */
public class Doc2Pdf {

    /**
     * 加载license 用于破解 不生成水印
     */
    private static void getLicense() throws Exception {
        try (InputStream is = Doc2Pdf.class.getClassLoader().getResourceAsStream("license.xml")) {
            License license = new License();
            license.setLicense(is);
        }
    }

    /**
     * word转pdf
     *
     * @param wordPath word文件保存的路径
     * @param pdfPath  转换后pdf文件保存的路径
     */
    public static void wordToPdf(String wordPath, String pdfPath) throws Exception {
        getLicense();
        File file = new File(pdfPath);
        try (FileOutputStream os = new FileOutputStream(file)) {
            Document doc = new Document(wordPath);
            doc.save(os, SaveFormat.PDF);
            FileCommonlyUsedUtil.close(os);
        }
    }

    public static void main(String[] args) throws Exception {
        wordToPdf("D:\\Projects\\ideaProjects\\quick-template\\quick-config\\src\\main\\resources\\template_file\\test.docx",
                "D:\\Projects\\ideaProjects\\quick-template\\quick-config\\src\\main\\resources\\template_file\\test.pdf");
    }
}
