#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'OGAWA YUITO'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import datetime
# import locale
import os
import os.path
import re
import subprocess

from PIL import Image

#from downloader import Downloader
from csv2html.downloader import Downloader
from csv2html.io import IO
#from table import Table
from csv2html.table import Table
from csv2html.tuple import Tuple
#from writer import Writer
from csv2html.writer import Writer

# pylint: disable=R0201
# R0201: Method could be a function (no-self-use)

class Translator:
	"""トランスレータ：CSVファイルをHTMLページへと変換するプログラム。"""

	def __init__(self, classOfAttributes):
		"""トランスレータのコンストラクタ。"""

		super().__init__()
		classOfAttributes.flush_base_directory()
		self._input_table = Table('input', classOfAttributes)
		self._output_table = Table('output', classOfAttributes)

	def compute_string_of_days(self, period):
		"""在位日数を計算して、それを文字列にして応答する。"""
		
		start,end = period.split("〜")
		start = list(map(int,re.split('年|月|日',start)[:-1]))
		start = datetime.datetime(start[0],start[1],start[2])
		if end == "":
			end = datetime.datetime.now()
		else:
			end = list(map(int, re.split('年|月|日', end)[:-1]))
			end = datetime.datetime(end[0],end[1],end[2])

		return (f"{(end-start).days+1}")

	def compute_string_of_image(self, a_tuple):
		"""サムネイル画像から画像へ飛ぶためのHTML文字列を作成して、それを応答する。"""

		number = a_tuple.values()[self._input_table.attributes().keys().index("no")]
		image_file = a_tuple.values()[self._input_table.attributes().keys().index("image")]
		thumbnail_file = a_tuple.values()[self._input_table.attributes().keys().index("thumbnail")]
		base_dir = self._output_table.attributes().base_directory()

		with Image.open(base_dir + os.sep + thumbnail_file) as a_image:
			width, height = a_image.size
		return f'<a name="{number}" href="{image_file}"> <img class="borderless" \
			src={thumbnail_file} width="{width}" height="{height}" alt="{thumbnail_file}"></a>'

	def execute(self):
		"""CSVファイルをHTMLページへと変換する。"""

		# ダウンローダに必要なファイル群をすべてダウンロードしてもらい、
		# 入力となるテーブルを獲得する。
		a_downloader = Downloader(self._input_table)
		a_downloader.perform()

		# トランスレータに入力となるテーブルを渡して変換してもらい、
		# 出力となるテーブルを獲得する。
		# print(self._input_table)
		self.translate()
		# print(self._output_table)

		# ライタに出力となるテーブルを渡して、
		# Webページを作成してもらう。
		a_writer = Writer(self._output_table)
		a_writer.perform()

		# 作成したページをウェブブラウザで閲覧する。
		class_attributes = self._output_table.attributes().__class__
		base_directory = class_attributes.base_directory()
		index_html = class_attributes.index_html()
		a_command = "open -a 'Safari' " + base_directory + os.sep + index_html
		subprocess.getoutput(a_command)

	@classmethod
	def perform(cls, class_attributes):
		"""属性リストのクラスを受け取り、CSVファイルをHTMLページへと変換する。"""

		# トランスレータのインスタンスを生成する。
		a_translator = cls(class_attributes)
		# トランスレータにCSVファイルをHTMLページへ変換するように依頼する。
		a_translator.execute()

	def translate(self):
		"""CSVファイルを基にしたテーブルから、HTMLページを基にするテーブルに変換する。"""

		# 名前を設定する
		input_key = self._input_table.attributes().keys()
		output_key = self._output_table.attributes().keys()
		for index, key in enumerate(output_key):
			if key in input_key:
				self._output_table.attributes().names()[index] = self._input_table.attributes().names()[input_key.index(key)]
			elif key == "days":
				self._output_table.attributes().names()[index] = "在位日数"

		# タプルを設定する
		for a_tuple in self._input_table.tuples():
			values = []
			for key in self._output_table.attributes().keys():
				if key == "days":
					print(a_tuple.values()[input_key.index("period")])
					values.append(self.compute_string_of_days(a_tuple.values()[input_key.index("period")]))
				elif key == "image":
					values.append(self.compute_string_of_image(a_tuple))
				elif key in input_key:
					values.append(IO.html_canonical_string(a_tuple.values()[input_key.index(key)]))

			self._output_table.add(Tuple(self._output_table.attributes(), values))

		(lambda x: x)(self) # NOP
