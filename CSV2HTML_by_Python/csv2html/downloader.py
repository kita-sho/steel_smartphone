#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'KITAZAWA SHOTA'
__version__ = '1.0.8' 
__date__ = '2024/12/14 (Created: 2024/12/14)'

import os
import shutil
import urllib.request
import threading

from csv2html.io import IO
from csv2html.reader import Reader

class Downloader(IO):
	"""ダウンローダ：CSVファイル・画像ファイル・サムネイル画像ファイルをダウンロードする。"""

	def __init__(self, input_table):
		"""ダウンローダのコンストラクタ。"""

		super().__init__(input_table)
		(lambda x: x)(input_table) # NOP

	def download_csv(self):
		"""情報を記したCSVファイルをダウンロードする。"""
		csv_url = super().attributes().csv_url()
		last_path = csv_url.split('/')[-1]
		file_path = super().attributes().base_directory() + os.sep + last_path
		
		try:
			urllib.request.urlretrieve(csv_url, file_path)
		except Exception as e:
			print(e)

	def download_images(self, image_filenames):
		"""画像ファイル群または縮小画像ファイル群をダウンロードする。"""
		last_path = image_filenames[0].split('/')[0]
		local_directory_path = super().attributes().base_directory() + os.sep + last_path
		os.makedirs(local_directory_path, exist_ok=True)
  
		#ここめっちゃ遅い byキタザワ
		for image_filename in image_filenames:
			image_url = super().attributes().base_url() + image_filename
			local_full_path = super().attributes().base_directory() + os.sep + image_filename
			try:
				urllib.request.urlretrieve(image_url, local_full_path)
			except Exception as e:
				print(e)
			

	def perform(self):
		"""すべて（情報を記したCSVファイル・画像ファイル群・縮小画像ファイル群）をダウンロードする。"""
		
		self.download_csv()
		reader = Reader(super().table())
		reader.perform()

		# self.download_images(super().table().image_filenames())
		# self.download_images(super().table().thumbnail_filenames())

		thread1 = threading.Thread(target=self.download_images, args=(super().table().image_filenames(),))
		thread2 = threading.Thread(target=self.download_images, args=(super().table().thumbnail_filenames(),))
		
		thread1.start()
		thread2.start()
  
		thread1.join()
		thread2.join()
  
		(lambda x: x)(self) # NOP
