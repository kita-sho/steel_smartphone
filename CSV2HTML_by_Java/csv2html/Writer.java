package csv2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import utility.StringUtility;

/**
 * ライタ：CSV情報のテーブルをHTMLページとして書き出す。
 * @author 北村拓海
 * @since 2024/12/01
 * @version 1.0
 */
public class Writer extends IO {
	

    /**
     * ライタのコンストラクタ。
     * @param aTable テーブル
     */
    public Writer(Table aTable) {
        super(aTable);
    }

    /**
     * HTMLページを基にするテーブルからインデックスファイル(index.html)に書き出す。
     */
    public void perform() {
        try {
            Attributes attributes = this.attributes();
            String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
            File aFile = new File(fileStringOfHTML);
            FileOutputStream outputStream = new FileOutputStream(aFile);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, StringUtility.encodingSymbol());
            BufferedWriter aWriter = new BufferedWriter(outputWriter);

            this.writeHeaderOn(aWriter);
            this.writeTableBodyOn(aWriter);
            this.writeFooterOn(aWriter);

            aWriter.close();
        } catch (UnsupportedEncodingException | FileNotFoundException anException) {
            anException.printStackTrace();
        } catch (IOException anException) {
            anException.printStackTrace();
        }
    }

    /**
     * 属性リストを書き出す。
     * @param aWriter ライタ
     */
    public void writeAttributesOn(BufferedWriter aWriter) throws IOException {
        aWriter.write("<tr>");
        aWriter.newLine();
        
        for (String attribute : this.table().attributes().names()) {
            aWriter.write("<td class=\"center-pink\"><strong>" + attribute + "</strong></td>");
            aWriter.newLine();
        }
        
        aWriter.write("</tr>");
        aWriter.newLine();
    }

    /**
     * フッタを書き出す。
     * @param aWriter ライタ
     */
    public void writeFooterOn(BufferedWriter aWriter) throws IOException {
        aWriter.write("</table>");
        aWriter.newLine();
        aWriter.write("</body>");
        aWriter.newLine();
        aWriter.write("</html>");
        aWriter.newLine();
    }

    /**
     * ヘッダを書き出す。
     * @param aWriter ライタ
     */
    public void writeHeaderOn(BufferedWriter aWriter) throws IOException {
        aWriter.write("<!DOCTYPE html>");
        aWriter.newLine();
        aWriter.write("<html>");
        aWriter.newLine();
        aWriter.write("<head>");
        aWriter.write("<meta charset=\"UTF-8\">");
        aWriter.write("<style>");
        aWriter.write("table { border-collapse: collapse; width: 100%; }");
        aWriter.write("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
        aWriter.write("th { background-color: #ffddbb; }");
        aWriter.write(".even-row { background-color: #ccffcc; }");
        aWriter.write(".odd-row { background-color: #ffddbb; }");
        aWriter.write("img { display: block; margin: auto; }");
        aWriter.write("</style>");
        aWriter.write("</head>");
        aWriter.newLine();
        aWriter.write("<body>");
        aWriter.newLine();
        aWriter.write("<table>");
        aWriter.newLine();
    }

    /**
     * ボディを書き出す。
     * @param aWriter ライタ
     */
    public void writeTableBodyOn(BufferedWriter aWriter) throws IOException {
        this.writeAttributesOn(aWriter); // 属性リスト（ヘッダー）を書き出す
        this.writeTuplesOn(aWriter); // データ行（タプル群）を書き出す
    }

    /**
     * タプル群を書き出す。
     * @param aWriter ライタ
     */
    private void writeTuplesOn(BufferedWriter aWriter) throws IOException {
        aWriter.write("<h1>総理大臣</h1>");
        int index = 0;
        for (Tuple aTuple : this.table().tuples()) {
            System.out.println("aTuple: " + aTuple);
            System.out.println("");
            aWriter.write("<tr>");
            aWriter.newLine();
            
            String rowClass = (index % 2 == 0) ? "even-row" : "odd-row";
            for (String aString : aTuple.values()) {
                aWriter.write("<td class=\"" + rowClass + "\">");
                
                // エスケープされたHTMLタグを元に戻して処理
                String unescapedString = aString
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&quot;", "\"")
                    .replace("&amp;", "&");
                    
                if (unescapedString.startsWith("<a href='images/") && unescapedString.endsWith("</a>")) {
                    // 画像タグをそのまま出力
                    aWriter.write(unescapedString);
                } else if (unescapedString.matches(".*\\d+\\.jpg$")) {
                    String imageNumber = unescapedString.replaceAll("[^0-9]", "");
                    aWriter.write("<a href=\"images/" + imageNumber + ".jpg\">");
                    aWriter.write("<img src=\"thumbnails/" + imageNumber + ".jpg\" " +
                                "width=\"32\" height=\"32\" " +
                                "alt=\"Image " + imageNumber + "\">");
                    aWriter.write("</a>");
                } else {
                    aWriter.write(IO.htmlCanonicalString(unescapedString));
                }
                
                aWriter.write("</td>");
                aWriter.newLine();
            }
            aWriter.write("</tr>");
            aWriter.newLine();
            index++;
        }
    }
}
