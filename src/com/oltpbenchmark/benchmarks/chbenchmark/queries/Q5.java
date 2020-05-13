/******************************************************************************
 *  Copyright 2015 by OLTPBenchmark Project                                   *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *    http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 ******************************************************************************/

package com.oltpbenchmark.benchmarks.chbenchmark.queries;

import com.oltpbenchmark.api.SQLStmt;
import com.oltpbenchmark.types.DatabaseType;

public class Q5 extends GenericQuery {

	public int get_query_index() { return 5;}
    public final SQLStmt query_stmt = new SQLStmt(
			"SELECT o_ol_cnt,\n" +
					"sum(CASE WHEN o_carrier_id = 1\n" +
					"OR o_carrier_id = 2 THEN 1 ELSE 0 END) AS high_line_count,\n" +
					"sum(CASE WHEN o_carrier_id <> 1\n" +
					"AND o_carrier_id <> 2 THEN 1 ELSE 0 END) AS low_line_count\n" +
					"FROM bmsql_oorder,\n" +
					"bmsql_order_line\n" +
					"WHERE ol_w_id = o_w_id\n" +
					"AND ol_d_id = o_d_id\n" +
					"AND ol_o_id = o_id\n" +
					"AND o_entry_d = ol_delivery_d\n" +
					"AND ol_delivery_d < timestamp'2020-05-12 04:00:00'\n" +
					"GROUP BY o_ol_cnt\n" +
					"ORDER BY o_ol_cnt limit 100"
        );

	public final SQLStmt tidb_query_stmt = new SQLStmt(
	        "SELECT o_ol_cnt,\n" +
					"sum(CASE WHEN o_carrier_id = 1\n" +
					"OR o_carrier_id = 2 THEN 1 ELSE 0 END) AS high_line_count,\n" +
					"sum(CASE WHEN o_carrier_id <> 1\n" +
					"AND o_carrier_id <> 2 THEN 1 ELSE 0 END) AS low_line_count\n" +
					"FROM oorder,\n" +
					"order_line\n" +
					"WHERE ol_w_id = o_w_id\n" +
					"AND ol_d_id = o_d_id\n" +
					"AND ol_o_id = o_id\n" +
					"AND o_entry_d <= ol_delivery_d\n" +
					"AND ol_delivery_d < timestamp'2020-01-01 00:00:00.000000'\n" +
					"GROUP BY o_ol_cnt\n" +
					"ORDER BY o_ol_cnt"
	);
		protected SQLStmt get_query(DatabaseType dbType) {
			if (dbType == DatabaseType.TiSPARK)
			    return tidb_query_stmt;
	    return query_stmt;
	}
}
