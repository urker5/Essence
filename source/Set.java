import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Set {
    private LinkedHashMap<String, String> set;
    private FileManager fman;
    private String defaultPath;

    public Set() {
        fman = new FileManager();

        // this line below is problematic and the whole system needs to be changed
        // defaultPath = "sets/";
        // try to set default path to current directory (will be same as .jar or .exe)
        set = null;
    }

    public boolean isNull() {
        return set == null;
    }

    public void openSet(String filename) {
        String path = defaultPath + filename + ".set";

        if (fman.fileExists(path)) {

            set = new LinkedHashMap<String, String>();

            try {
                set = fman.readFile(path);
                return;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.createSet(filename);
            return;
        }

    }

    public void createSet(String name) {
        fman.createFile(defaultPath + name + ".set");
        this.openSet(name);
    }

    public void deleteSet(String filename) {
        fman.deleteFile(defaultPath + filename + ".set");
        set = null;
    }

    public void saveSet(String name) {
        if (set != null) {
            fman.writeFile(set, defaultPath + name + ".set");
            return;
        }
        System.out.println("can't save to a null set");
    }

    public List<String> getTerms() {
        if (set != null) {

            List<String> mainList = new ArrayList<String>();
            mainList.addAll(set.keySet());
            return mainList;
        }
        System.out.println("can't get terms from null set");
        return null;
    }

    public List<String> getDefinitions() {
        if (set != null) {

            return new ArrayList<String>(set.values());
        }
        System.out.println("can't get definitions from null set");
        return null;
    }

    public void addTerm(String term, String definition) {
        if (set != null) {
            set.put(term, definition);
        }
    }

    public void deleteTerm(String term) {
        if (set != null) {
            set.remove(term);
        }
    }

    public void deleteTerm(ArrayList<String> terms) {
        if (set != null) {

            for (int i = 0; i < terms.size(); i++) {
                set.remove(terms.get(i));
            }
        }
    }

    public String get(String key) {
        return set.get(key);
    }

    public void clear() {
        set.clear();
    }

    public String toString() {
        return set.toString();
    }
}
