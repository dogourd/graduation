package top.ezttf.graduation.withoutspring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author yuwen
 * @date 2019/3/19
 */
public class One {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = reader.readLine();
        if (str != null && !str.equals("")) {
            char last = str.charAt(0);
            int count = 0;


            for (char c : str.toCharArray()) {
                if (c == last) {
                    count ++;
                } else {
                    if (count == 1) {
                        System.out.print(String.valueOf(last));
                    } else {
                        System.out.print(String.valueOf(last) + count);
                    }
                    count = 1;
                }
                last = c;
            }
        }

        reader.close();
    }
}
