package es.lhdev.notes.singletons;

public class TempData {
    private static final TempData ourInstance = new TempData();
    private String name;
    private String text;

    public static TempData getInstance() {
        return ourInstance;
    }

    private TempData() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
