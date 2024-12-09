package csv2html;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * トランスレータ：CSVファイルをHTMLページへと変換するプログラム。
 * @author 北村拓海
 * @since 2024/12/01
 * @version 1.0
 */
public class Translator extends Object {

    /**
     * CSVに由来するテーブルを記憶するフィールド。
     */
    private Table inputTable;

    /**
     * HTMLに由来するテーブルを記憶するフィールド。
     */
    private Table outputTable;

    /**
     * 属性リストのクラスを指定したトランスレータのコンストラクタ。
     *
     * @param classOfAttributes 属性リストのクラス
     */
    public Translator(Class<? extends Attributes> classOfAttributes) {
        super();

        Attributes.flushBaseDirectory();

        try {
            Constructor<? extends Attributes> aConstructor = classOfAttributes.getConstructor(String.class);

            this.inputTable = new Table(aConstructor.newInstance("input"));
            this.outputTable = new Table(aConstructor.newInstance("output"));
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException anException) {
            anException.printStackTrace();
        }

        return;
    }

    /**
     * 在位日数を計算して、それを文字列にして応答する。
     *
     * @param periodString 在位期間の文字列
     * @return 在位日数の文字列
     */
    public String computeNumberOfDays(String period) {
        try {
            String[] dates = period.split("〜");
            if (dates.length != 2) {
                return "不明";
            }

            String startDateStr = dates[0].trim();
            String endDateStr = dates[1].trim();

            String[] startParts = startDateStr.split("[年月日]");
            String[] endParts = endDateStr.split("[年月日]");

            if (startParts.length != 3 || endParts.length != 3) {
                return "不明";
            }

            LocalDate startDate = LocalDate.of(
                Integer.parseInt(startParts[0]),
                Integer.parseInt(startParts[1]),
                Integer.parseInt(startParts[2])
            );

            LocalDate endDate = LocalDate.of(
                Integer.parseInt(endParts[0]),
                Integer.parseInt(endParts[1]),
                Integer.parseInt(endParts[2])
            );

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            return String.valueOf(days);

        } catch (Exception e) {
            return "不明";
        }
    }    



    /**
     * サムネイル画像から画像へ飛ぶためのHTML文字列を作成して、それを応答する。
     *
     * @param aString 画像の文字列
     * @param aTuple タプル
     * @param no 番号
     * @return サムネイル画像から画像へ飛ぶためのHTML文字列
     */
    public String computeStringOfImage(String aString, Tuple aTuple, int no) {
        return String.format("<a href='%s'><img src='%s' alt='Image %d' /></a>", aString, aString, no);
    }
    

    /**
     * CSVファイルをHTMLページへ変換する。
     */
    public void execute() {
        // 必要な情報をダウンロードする。
        Downloader aDownloader = new Downloader(this.inputTable);
        aDownloader.perform();

        // CSVに由来するテーブルをHTMLに由来するテーブルへと変換する。
        System.out.println(this.inputTable);
        this.translate();
        System.out.println(this.outputTable);

        // HTMLに由来するテーブルから書き出す。
        Writer aWriter = new Writer(this.outputTable);
        aWriter.perform();


        // ブラウザを立ち上げて閲覧する。
        try {
            Attributes attributes = this.outputTable.attributes();
            String fileStringOfHTML = attributes.baseDirectory() + attributes.indexHTML();
            ProcessBuilder aProcessBuilder = new ProcessBuilder("open", "-a", "Safari", fileStringOfHTML);
            aProcessBuilder.start();
        } catch (Exception anException) {
            anException.printStackTrace();
        }

        return;
    }

    /**
     * 属性リストのクラスを受け取って、CSVファイルをHTMLページへと変換するクラスメソッド。
     *
     * @param classOfAttributes 属性リストのクラス
     */
    public static void perform(Class<? extends Attributes> classOfAttributes) {
        // トランスレータのインスタンスを生成する。
        Translator aTranslator = new Translator(classOfAttributes);
        // トランスレータにCSVファイルをHTMLページへ変換するように依頼する。
        aTranslator.execute();

        return;
    }

    /**
     * CSVファイルを基にしたテーブルから、HTMLページを基にするテーブルに変換する。
     */
    public void translate() {
        // デバッグ出力を追加
        System.out.println("\n=== Before Translation ===");
        System.out.println("Input Table Tuples Size: " + this.inputTable.tuples().size());
        if (!this.inputTable.tuples().isEmpty()) {
            System.out.println("First Input Tuple: " + this.inputTable.tuples().get(0).values());
        }

        // 新たに設定する属性リストの�ーと名前（CSVに存在しない特殊カラムのみ定義）
        Map<String, String> specialColumnNames = new HashMap<>();
        specialColumnNames.put("days", "在位日数");

        // outputTableの属性リストの名前を設定
        this.outputTable.attributes().keys().forEach((key) -> {
            Attributes outputAttributes = this.outputTable.attributes();
            Attributes inputAttributes = this.inputTable.attributes();
            Integer indexOfOutputKey = outputAttributes.indexOf(key);
            Integer indexOfInputKey = inputAttributes.indexOf(key);

            String columnName;
            if (specialColumnNames.containsKey(key)) {
                // 特殊カラム（な�）の場合は定義済みの名前を使用
                columnName = specialColumnNames.get(key);
            } else if (indexOfInputKey != -1) {
                // 入力CSVに存在するカラムの�合はCSVの属性名を使用
                columnName = inputAttributes.nameAt(indexOfInputKey);
                if (columnName == null || columnName.isEmpty()) {
                    // 属性名が空の場合はキーを使用
                    columnName = key;
                }
            } else {
                // どちらにも該当�い場合は未定義とする
                columnName = "未定義";
            }
            
            System.out.println("Setting column name for " + key + ": " + columnName);
            outputAttributes.names().set(indexOfOutputKey, IO.htmlCanonicalString(columnName));
        });

        // outputTableにタプルの要素を追加
        this.inputTable.tuples().forEach((aTuple) -> {
            Attributes inputAttributes = this.inputTable.attributes();
            List<String> tupleValues = new ArrayList<>();

            this.outputTable.attributes().keys().forEach((key) -> {
                String aValue = "";
                Integer inputIndex = inputAttributes.indexOf(key);
                
                if ("days".equals(key)) {
                    // 在位日数を入れる
                    String periodValue = aTuple.values().get(inputAttributes.indexOf("period"));
                    aValue = this.computeNumberOfDays(periodValue);
                    System.out.println("Computing days for period: " + periodValue + " -> " + aValue);
                } else if ("image".equals(key)) {
                    // 画像のHTML文を入れる
                    String imagePath = aTuple.values().get(inputAttributes.indexOfImage());
                    String no = aTuple.values().get(inputAttributes.indexOfNo());
                    aValue = this.computeStringOfImage(imagePath, aTuple, Integer.valueOf(no));
                    System.out.println("Computing image HTML for: " + imagePath);
                } else if (inputIndex != -1) {
                    // 通常のカラムの値を取得
                    aValue = aTuple.values().get(inputIndex);
                    System.out.println("Copying value for " + key + ": " + aValue);
                }
                
                tupleValues.add(IO.htmlCanonicalString(aValue));
            });

            this.outputTable.add(new Tuple(this.outputTable.attributes(), tupleValues));
        });

        // 変換後の状態を確認
        System.out.println("\n=== After Translation ===");
        System.out.println("Output Table Tuples Size: " + this.outputTable.tuples().size());
        if (!this.outputTable.tuples().isEmpty()) {
            System.out.println("First Output Tuple: " + this.outputTable.tuples().get(0).values());
        }

        return;
    }
      
}
