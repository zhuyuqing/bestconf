#!/bin/bash
kill -9 $(ps -ef|grep sysbench |awk '{print $2}')
echo ok
