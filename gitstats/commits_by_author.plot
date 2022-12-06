set terminal png transparent size 640,240
set size 1.0,1.0

set terminal png transparent size 640,480
set output 'commits_by_author.png'
set key left top
set yrange [0:]
set xdata time
set timefmt "%s"
set format x "%Y-%m-%d"
set grid y
set ylabel "Commits"
set xtics rotate
set bmargin 6
plot 'commits_by_author.dat' using 1:2 title "liangxiao6" w lines, 'commits_by_author.dat' using 1:3 title "宋时成" w lines, 'commits_by_author.dat' using 1:4 title "侯杰" w lines, 'commits_by_author.dat' using 1:5 title "lihongwei" w lines, 'commits_by_author.dat' using 1:6 title "梁肖" w lines, 'commits_by_author.dat' using 1:7 title "zuoxl" w lines, 'commits_by_author.dat' using 1:8 title "左晓麟" w lines, 'commits_by_author.dat' using 1:9 title "李鸿伟" w lines, 'commits_by_author.dat' using 1:10 title "dongben" w lines, 'commits_by_author.dat' using 1:11 title "houjie" w lines, 'commits_by_author.dat' using 1:12 title "董犇" w lines, 'commits_by_author.dat' using 1:13 title "小安2012" w lines, 'commits_by_author.dat' using 1:14 title "[houjie]" w lines, 'commits_by_author.dat' using 1:15 title "954703900@qq.com" w lines
