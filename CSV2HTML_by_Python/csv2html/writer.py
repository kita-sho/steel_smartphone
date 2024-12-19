#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""CSV2HTML：総理大臣と徳川幕府の情報「CSV」からWebページ「HTML」を生成。"""
__author__ = 'OGAWA YUITO'
__version__ = '1.0.7'
__date__ = '2021/01/10 (Created: 2016/01/01)'

import datetime
import os

from csv2html.io import IO

# pylint: disable=R0201
# R0201: Method could be a function (no-self-use)

class Writer(IO):
	"""ライタ：情報のテーブルをHTMLページとして書き出す。"""

	def __init__(self, output_table):
		"""ライタのコンストラクタ。HTMLページを基にするテーブルを受け取る。"""

		super().__init__(output_table)
		(lambda x: x)(output_table) # NOP

	def perform(self):
		"""HTMLページを基にするテーブルから、インデックスファイル(index_html)に書き出す。"""

		class_attributes = self.attributes().__class__
		base_directory = class_attributes.base_directory()
		index_html = class_attributes.index_html()

		html_filename = os.path.join(base_directory, index_html)
		with open(html_filename, 'w', encoding='utf-8') as a_file:
			self.write_header(a_file)
			self.write_body(a_file)
			self.write_footer(a_file)

	def write_body(self, file):
		"""ボディを書き出す。つまり、属性リストを書き出し、タプル群を書き出す。"""

		file.write("<tr>")
		file.write("\n")
	
		for attribute in self.table().attributes().names():
			print(attribute)
			file.write("<td class=\"center-pink\"><strong>" + attribute + "</strong></td>")
			file.write("\n")
        
		file.write("<tr>")
		file.write("\n")

		file.write("<h1>総理大臣</h1>")
		file.write("\n")

		index = 0
		for a_tuple in self.table().tuples():	
			file.write("<tr>")
			file.write("\n")

			row_class = "even-row" if index % 2 == 0 else "odd-row"	
				
			for value in a_tuple.values():
				file.write(f"<td class=\"{row_class}\">")
				file.write(str(value))
				print(str(value))	
				# エスケープされたHTMLタグを元に戻して処理
				"""unescaped_string = (
					value.replace("&lt;", "<")
					.replace("&gt;", ">")
					.replace("&quot;", "\"")
					.replace("&amp;", "&")
				)

				if unescaped_string.startswith("<a href='images/") and unescaped_string.endswith("</a>"):
					# 画像タグをそのまま出力
					file.write(unescaped_string)
				elif unescaped_string.endswith(".jpg") and unescaped_string.isdigit():
					image_number = ''.join(filter(str.isdigit, unescaped_string))
					file.write(f"<a href=\"images/{image_number}.jpg\">")
					file.write(f"<img src=\"thumbnails/{image_number}.jpg\" " +
									f"width=\"32\" height=\"32\" " +
									f"alt=\"Image {image_number}\">")
					file.write("</a>")
				else:
					file.write(self.html_canonical_string(unescaped_string))"""		
				file.write("</td>")
				file.write("\n")	
			file.write("</tr>")
			file.write("\n")
			index += 1
		(lambda x: x)(file) # NOP

	def write_footer(self, file):
		"""フッタを書き出す。"""

		file.write("</table>")
		file.write("\n")
		file.write("</body>")
		file.write("\n")
		file.write("</html>")
		file.write("\n")
		(lambda x: x)(file) # NOP

	def write_header(self, file):
		"""ヘッダを書き出す。"""

		file.write("<!DOCTYPE html>")
		file.write("\n")
		file.write("<html>")
		file.write("\n")
		file.write("<head>")
		file.write("<meta charset=\"UTF-8\">")
		file.write("<style>")
		file.write("table { border-collapse: collapse width: 100% }")
		file.write("th, td { border: 1px solid black; padding: 8px; text-align: left; }")
		file.write("th { background-color: #ffddbb; }")
		file.write(".even-row { background-color: #ccffcc; }")
		file.write(".odd-row { background-color: #ffddbb; }")
		file.write("img { display: block; margin: auto; }")
		file.write("</style>")
		file.write("</head>")
		file.write("\n")
		file.write("<body>")
		file.write("\n")
		file.write("<table>")
		file.write("\n")

		(lambda x: x)(file) # NOP
