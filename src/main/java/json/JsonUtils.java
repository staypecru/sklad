package json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

public class JsonUtils {

    public static JSONObject getInputParameters(BufferedReader reader) {
        StringBuffer stringBuffer = getStringBufferWithInputParameters(reader);
        return parseStringBufferToJsonObject(stringBuffer);
    }


    private static StringBuffer getStringBufferWithInputParameters(BufferedReader reader) {
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    private static JSONObject parseStringBufferToJsonObject(StringBuffer stringBuffer) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuffer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
