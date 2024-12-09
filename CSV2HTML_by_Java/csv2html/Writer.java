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

        for (String header : this.attributes().names()) {
            aWriter.write("<th class=\"center-pink\"><strong>" + header + "</strong></th>");
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
        aWriter.write("<style>table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid black; padding: 8px; text-align: left; } th { background-color: #f2f2f2; }</style>");
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
    public void writeTuplesOn(BufferedWriter aWriter) throws IOException {
        // テーブルからタプル群を取得
        List<Tuple> tuples = this.table().tuples();
    
        // 各行（タプル）を書き出す
        for (Tuple tuple : tuples) {
            aWriter.write("<tr>");
            aWriter.newLine();
    
            // タプルの各セルの値を取り出し、書き出す
            for (String cell : tuple.values()) {
                // &nbsp; を削除
                String cleanedCell = cell.replace("&nbsp;", "");
                aWriter.write("<td>" + IO.htmlCanonicalString(cleanedCell) + "</td>");
                aWriter.newLine();
            }
    
            aWriter.write("</tr>");
            aWriter.newLine();
        }
    }
    
}
