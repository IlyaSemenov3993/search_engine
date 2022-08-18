package ru.skillbox.searcher.process.website.tools;

public enum ForbiddenDomains {
    DOC (".doc"),
    DOCX (".docx"),
    EPS (".eps"),
    FIG (".fig"),
    GIF (".gif"),
    JPG(".jpg") ,
    JPEG (".jpeg"),
    M (".m"),
    NC (".nc"),
    PDF (".pdf"),
    PHP (".php"),
    PNG (".png"),
    PPT (".ppt"),
    PPTX (".pptx"),
    XLS  (".xls"),
    XLSX (".xlsx"),
    ZIP (".zip");

    private String value;

    ForbiddenDomains(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
