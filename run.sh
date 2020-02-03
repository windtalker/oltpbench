#!/bin/bash

dbType="tidb"
if [ -n "$1" ]
then
	dbType=$1
fi
apThreads="2 4 8 16 32 64 0"
if [ -n "$2" ]
then
	apThreads=$2
fi
tpThreads="64 128 256 512 1024"
if [ -n "$3" ]
then
	tpThreads=$3
fi
wd=100
if [ -n "$4" ]
then
	wd=$4
fi

tmpResultDir="results"
if [ -d "$tmpResultDir" ]
then
	echo $tmpResultDir already exists
	exit 1
fi

resultDir=result_${dbType}_wd_${wd}_$(date +%F-%R:%S)
if [ -d "resultDir" ]
then
	echo $resultDir already exists
	exit 1
fi
mkdir $resultDir

for ap in $apThreads
do 
	for tp in $tpThreads
	do
		if [ $dbType = "memsql" ]
		then	
			mysql -uroot -pmemsql -P 3339 -h 172.16.4.41 -e "create database if not exists chbenchmark "

			sed "s/<scalefactor>.*<\/scalefactor>/<scalefactor>${wd}<\/scalefactor>/g" config/chbenchmark_memsql_load.xml > config/chbenchmark_memsql_load_tmp.xml
			./oltpbenchmark -b tpcc,chbenchmark -c config/chbenchmark_memsql_load_tmp.xml --create=true --load=true
			sleep 300
			sed "s/<terminals bench=\"tpcc\">.*<\/terminals>/<terminals bench=\"tpcc\">${wd}<\/terminals>/g" config/chbenchmark_memsql.xml > config/chbenchmark_memsql_tp.xml
			./oltpbenchmark -b tpcc -c config/chbenchmark_memsql_tp.xml --execute=true   -o outputfile_memsql_ap_${ap}_tp_${tp}_wd_${wd}_tp &
		
			if [ ${ap} -ne 0 ]
			then
				sed "s/<terminals bench=\"tpcc\">.*<\/terminals>/<terminals bench=\"tpcc\">${wd}<\/terminals>/g" config/chbenchmark_memsql.xml > config/chbenchmark_memsql_ap.xml
				./oltpbenchmark -b chbenchmark -c config/chbenchmark_memsql_ap.xml --execute=true   -o outputfile_memsql_ap_${ap}_tp_${tp}_wd_${wd}_ap &
			fi
			wait

			echo "Memsql count of order_line after ap_${ap}_tp_${tp}_wd_${wd}" >> $resultDir/row_count
			mysql -uroot -pmemsql -P 3339 -h 172.16.4.41 -e "select count(*) from chbenchmark.order_line" >> $resultDir/row_count
			mysql -uroot -pmemsql -P 3339 -h 172.16.4.41 -e "drop database if exists chbenchmark"
		elif [ $dbType = "tidb" ]
		then
			if [ ${ap} -ne 0 ]
			then
				sed "s/<scalefactor>.*<\/scalefactor>/<scalefactor>${wd}<\/scalefactor>/g" config/chbenchmark_tidb_load.xml > config/chbenchmark_tidb_load_tmp.xml
				./oltpbenchmark -b tpcc,chbenchmark -c config/chbenchmark_tidb_load_tmp.xml --create=true --load=true
			else
				sed "s/<scalefactor>.*<\/scalefactor>/<scalefactor>${wd}<\/scalefactor>/g" config/chbenchmark_tikv_load.xml > config/chbenchmark_tikv_load_tmp.xml
				./oltpbenchmark -b tpcc,chbenchmark -c config/chbenchmark_tikv_load_tmp.xml --create=true --load=true
			fi
			ssh 172.16.4.44 "echo \"\" > /data1/xufei/tidb_ansible/log/tiflash.log"
			ssh 172.16.4.69 "echo \"\" > /data1/xufei/tidb_ansible/log/tiflash.log"
			ssh 172.16.5.55 "echo \"\" > /data1/xufei/tidb_ansible/log/tiflash.log"
			sed "s/<terminals bench=\"chbenchmark\">.*<\/terminals>/<terminals bench=\"chbenchmark\">${wd}<\/terminals>/g" config/chbenchmark_tidb.xml > config/chbenchmark_tidb_tp.xml
			./oltpbenchmark -b tpcc -c config/chbenchmark_tidb_tp.xml --execute=true   -o outputfile_tidb_ap_${ap}_tp_${tp}_wd_${wd}_tp &
			if [ ${ap} -ne 0 ]
			then
				sed "s/<terminals bench=\"chbenchmark\">.*<\/terminals>/<terminals bench=\"chbenchmark\">${wd}<\/terminals>/g" config/chbenchmark_tispark.xml > config/chbenchmark_tispark_ap.xml
				./oltpbenchmark -b chbenchmark -c config/chbenchmark_tispark_ap.xml --execute=true   -o outputfile_tidb_ap_${ap}_tp_${tp}_wd_${wd}_ap &
			fi
			wait

			echo "TiDB count of order_line after ap_${ap}_tp_${tp}_wd_${wd}" >> $resultDir/row_count
			mysql -uroot -P 4000 -h 172.16.4.75 -e "select count(*) from chbenchmark.order_line" >> $resultDir/row_count
			if [ ${ap} -ne 0 ]
			then
				scp 172.16.4.44:/data1/xufei/tidb_ansible/log/tiflash.log ./$resultDir/tiflash_44.log.ap_${ap}_tp_${tp}
				scp 172.16.4.69:/data1/xufei/tidb_ansible/log/tiflash.log ./$resultDir/tiflash_69.log.ap_${ap}_tp_${tp}
				scp 172.16.5.55:/data1/xufei/tidb_ansible/log/tiflash.log ./$resultDir/tiflash_55.log.ap_${ap}_tp_${tp}
			fi
		else
			echo "dbType should be memsql or tidb"
		fi
	done
done

if [ -d "$tmpResultDir" ]
then 
	mv $tmpResultDir $resultDir
else
	echo "Failed to find result files, dir $tmpResultDir not exists"
	exit 1
fi
