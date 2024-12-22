package csv2html;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import utility.ImageUtility;
import utility.StringUtility;

/**
 * ダウンローダ：CSVファイル・画像ファイル・サムネイル画像ファイルをダウンロードする。
 * @author 北澤昇大
 * @since 2024/12/9
 * @version 1.0.1 Threadを用いたダウンロードに変更
 */
public class Downloader extends IO
{
	/**
	 * ダウンローダのコンストラクタ。
	 * @param aTable テーブル
	 */
	public Downloader(Table aTable)
	{
		super(aTable);

		return;
	}

	/**
	 * 総理大臣の情報を記したCSVファイルをダウンロードする。
	 */
	public void downloadCSV()
	{
		String csvUrl = super.attributes().csvUrl();
		List<String> splitUrl = IO.splitString(csvUrl, "/");
		File csvFile = new File(super.attributes().baseDirectory(), splitUrl.get(splitUrl.size() - 1));
		List<String> csvText = IO.readTextFromURL(csvUrl);
		IO.writeText(csvText, csvFile);
		return;
	}
	
	/**
	 * 総理大臣の画像群をダウンロードする。
	 */
	public void downloadImages()
	{
		int indexOfImage = this.attributes().indexOfImage();
		this.downloadPictures(indexOfImage);

		return;
	}

	/**
	 * 総理大臣の画像群またはサムネイル画像群をダウンロードする。
	 * @param indexOfPicture 画像のインデックス
	 */
	private void downloadPictures(int indexOfPicture)
	{
		List<Tuple> tuples = super.table().tuples();
		// System.out.println("Listの中のタプルの中の一要素を抜き出す"+tuples.get(1).values().get(indexOfPicture));
		for(int i=0;i<tuples.size();i++){
			String pictureName = tuples.get(i).values().get(indexOfPicture);
			File picturesFile = new File(super.attributes().baseDirectory(), pictureName);
			//System.out.println("picturename"+pictureName);
			BufferedImage anImage = ImageUtility.readImageFromURL(super.attributes().baseUrl() + pictureName);
			picturesFile.getParentFile().mkdirs();
			ImageUtility.writeImage(anImage, picturesFile);
		}
		
		return;
	}

	/**
	 * 総理大臣の画像群をダウンロードする。
	 */
	public void downloadThumbnails()
	{
		int indexOfThumbnail = this.attributes().indexOfThumbnail();
		this.downloadPictures(indexOfThumbnail);

		return;
	}

	/**
	 * 総理大臣の情報を記したCSVファイルをダウンロードして、画像群やサムネイル画像群もダウロードする。
	 */
	public void perform()
	{
		this.downloadCSV();
		Reader reader = new Reader(super.table());
		reader.perform();
		
		// downloadImages();
		// downloadThumbnails();

		Thread downloadImageThread = new Thread(new Runnable() {
			@Override
			public void run() {
				downloadImages();
			}
		});
		
		Thread downloadThumbnailsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				downloadThumbnails();
			}
		});
		
		downloadImageThread.start();
		downloadThumbnailsThread.start();
		
		try {
			downloadImageThread.join();
			downloadThumbnailsThread.join();
		} catch (InterruptedException exception) {
			System.out.println(exception);
		}
		
		return;
	}
}
