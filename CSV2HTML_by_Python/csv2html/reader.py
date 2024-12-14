#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'KiTAZAWA SHOTA'
__version__ = '1.1.0'
__date__ = '2024/12/14 (Created: 2024/12/14)'

import os

from csv2html.io import IO
from csv2html.tuple import Tuple

class Reader(IO):
	"""リーダ：情報を記したCSVファイルを読み込んでテーブルに仕立て上げる。"""

	def __init__(self, input_table):
		"""リーダのコンストラクタ。"""

		super().__init__(input_table)
		(lambda x: x)(input_table) # NOP

	def perform(self):
		"""ダウンロードしたCSVファイルを読み込む。"""
		last_path = super().attributes().csv_url().split('/')[-1]
		csv_file = super().attributes().base_directory() + os.sep + last_path
		csv_list = super().read_csv(csv_file)
		
		for index, key in enumerate(csv_list[0]):
			super().attributes().names()[index] = key

		for row in csv_list[1:]:
			tuple = Tuple(super().attributes(),row)
			print(tuple)
			super().table().add(tuple)
   

		(lambda x: x)(self) # NOP
