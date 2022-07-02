package com.foundation.theory.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/10/19 17:10
 */
public class VisitorDemo {
    public static void main(String[] args) {
        List<BaseResourceFile> files = new ArrayList<>();
        Extractor extractor = new Extractor();
        files.forEach(e -> e.accept(extractor));
    }
}
abstract class BaseResourceFile {
    private String filePath;
    public abstract void accept(Visitor visitor);
}
class PdfFile extends BaseResourceFile{
    @Override
    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}
class WordFile extends BaseResourceFile{
    @Override
    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}
interface Visitor {
    void accept(PdfFile file);
    void accept(WordFile file);
}
class Extractor implements Visitor {
    @Override
    public void accept(PdfFile file) {
        System.out.println("extract pdf file");
    }
    @Override
    public void accept(WordFile file) {
        System.out.println("extract word file");
    }
}
class Compressor implements Visitor {
    @Override
    public void accept(PdfFile file) {
        System.out.println("compress pdf file");
    }
    @Override
    public void accept(WordFile file) {
        System.out.println("compress word file");
    }
}