package org.jeecg.modules.system.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptUtil {

    /**
     * 解析 Lua
     */
    public static String getScript(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = ScriptUtil.class.getClassLoader().getResourceAsStream(path);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String str;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
