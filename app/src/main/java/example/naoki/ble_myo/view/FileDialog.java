package example.naoki.ble_myo.view;

import android.app.ActionBar;
import android.app.Dialog;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import example.naoki.ble_myo.Activity.AnalysisActivity;

/**
 * Created by Sabarada on 2016-06-25.
 */
public class FileDialog {

    private static final String PARENT_DIR = "/..";

    private final AnalysisActivity analysisActivity;
    private ListView list;
    private Dialog dialog;
    private File currentPath;

    private String extension = null;
    private FileSelectedListener fileListener;

    public void setExtension(String extension)
    {
        this.extension = (extension == null) ? null : extension.toLowerCase();
    }

    // file selection event handling
    public interface FileSelectedListener{
        void fileSelected(File file);
    }

    public FileDialog setFileListener(FileSelectedListener fileListener)
    {
        this.fileListener = fileListener;
        return this;
    }

    public FileDialog(AnalysisActivity analysisActivity)
    {
        this.analysisActivity = analysisActivity;
        dialog = new Dialog(analysisActivity);
        list = new ListView(analysisActivity);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileChosen = (String) list.getItemAtPosition(position);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    refresh(chosenFile);
                } else {
                    if (fileListener != null) {
                        fileListener.fileSelected(chosenFile);
                    }
                    dialog.dismiss();
                }

            }
        });

        dialog.setContentView(list);
        dialog.getWindow().setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        refresh(new File(Environment.getExternalStorageDirectory() + "/data/Myo/rawdata/"));
//        refresh(new File(Environment.getExternalStorageDirectory() ));

    }

    public void showDialog()
    {
        dialog.show();
    }

    private void refresh(File path){

        this.currentPath = path;

        if(path.exists())
        {
            System.out.println(path.toString());

            File[] dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead());
                }
            });


            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if(!file.isDirectory()){
                        if(!file.canRead()){
                            return false;
                        }
                        else if(extension == null)
                        {
                            return true;
                        }
                        else{
                            return file.getName().toLowerCase().endsWith(extension);
                        }
                    } else{
                        return false;
                    }
                }
            });

            int i = 0;
            String[] fileList;

            if(path.getParentFile() == null)
            {
                fileList = new String[dirs.length + files.length];
            }else {
                fileList = new String[dirs.length + files.length + 1];
                fileList[i++] = PARENT_DIR;
            }

            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) { fileList[i++] = dir.getName(); }
            for (File file : files ) { fileList[i++] = file.getName(); }


            dialog.setTitle(currentPath.getPath());
            list.setAdapter(new ArrayAdapter(analysisActivity,
                    android.R.layout.simple_list_item_1, fileList) {
                @Override
                public View getView(int pos, View view, ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((TextView) view).setSingleLine(true);
                    return view;
                }
            });
        }
    }

    private File getChosenFile(String fileChosen){
        if(fileChosen.equals(PARENT_DIR)){
            return currentPath.getParentFile();
        }
        else{
            return new File(currentPath, fileChosen);
        }
    }
}
