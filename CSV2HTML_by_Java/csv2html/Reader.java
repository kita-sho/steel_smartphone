package csv2html;

import java.io.File;
import java.util.List;

/**
 * リーダ：情報を記したCSVファイルを読み込んでテーブルに仕立て上げる。
 */
public class Reader extends IO
{
	/**
	 * リーダのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Reader(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * ダウンロードしたCSVファイルを読み込む。
	 */
	public void perform()
	{
		String csvUrl = super.attributes().csvUrl();
		List<String> separateListUrl = IO.splitString(csvUrl,"/");
		File csvFile = new File(super.attributes().baseDirectory(),separateListUrl.get(separateListUrl.size() - 1));
		List<String> csvListText = IO.readTextFromFile(csvFile);

		/*
		 * ここは正規表現で書ける可能性があるかも by北澤 12月9日
		 */
		String name = csvListText.get(0);
		super.attributes().names(IO.splitString(name,","));
		//System.out.println(IO.splitString(name,","));

		/*
		 * for文で書いてるけどもっとスマートなやり方があるかも
		 */
		for(int i=1;i<csvListText.size();i++){
			List<String> splitText = IO.splitString(csvListText.get(i),",");
			Tuple aTuple = new Tuple(super.attributes(), splitText);
			super.table().add(aTuple);
		}


		return;
	}
}
