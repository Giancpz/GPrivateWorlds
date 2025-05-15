package org.giancpz.gprivateworlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Template
{
    public static void LoadTemplate()
    {
        String td = "worldtemplate";
        List<String> fileList = new ArrayList<>();

        fileList.add("region");
        fileList.add("level.dat");
        fileList.add("session.lock");

        File template = new File(td);

        if(template.exists())
        {
            for (File file : template.listFiles())
            {
                if(fileList.contains(file.getName())) continue;
                file.delete();
            }

            Main.Singleton().templatechecksum =  Checksum.Get(td + "/region");

            try {
                Zip.zip(td,"worldtemplate.zip");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
