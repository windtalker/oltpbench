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

public class Q7 extends GenericQuery {

	public int get_query_index() { return 7;}
    public final SQLStmt query_stmt = new SQLStmt(
            "SELECT /*+ tidb_bcj(bmsql_order_line,bmsql_oorder,bmsql_customer) */ c_last,\n" +
					"c_id,\n" +
					"o_id,\n" +
					"o_entry_d,\n" +
					"o_ol_cnt,\n" +
					"sum(ol_amount) AS amount_sum\n" +
					"FROM\n" +
					"bmsql_order_line,\n" +
					"bmsql_oorder,\n" +
					"bmsql_customer\n" +
					"WHERE c_id = o_c_id\n" +
					"AND c_w_id = o_w_id\n" +
					"AND c_d_id = o_d_id\n" +
					"AND ol_w_id = o_w_id\n" +
					"AND ol_d_id = o_d_id\n" +
					"AND ol_o_id = o_id\n" +
					"AND c_state LIKE 'A%'\n" +
					"AND o_entry_d > timestamp'2020-05-14 13:00:40'\n" +
					"GROUP BY o_id,\n" +
					"o_w_id,\n" +
					"o_d_id,\n" +
					"c_id,\n" +
					"c_last,\n" +
					"o_entry_d,\n" +
					"o_ol_cnt HAVING sum(ol_amount) > 200\n" +
					"ORDER BY amount_sum DESC, o_entry_d\n" +
					"limit 100"
        );

	public final SQLStmt tidb_query_stmt = new SQLStmt(
			"SELECT su_nationkey AS supp_nation, "
					+        "substring(c_state ,1 ,1) AS cust_nation, "
					+        "year(o_entry_d) AS l_year, "
					+        "sum(ol_amount) AS revenue "
					+ "FROM supplier, "
					+      "stock, "
					+      "order_line, "
					+      "oorder, "
					+      "customer, "
					+      "nation n1, "
					+      "nation n2 "
					+ "WHERE ol_supply_w_id = s_w_id "
					+   "AND ol_i_id = s_i_id "
					+   "AND MOD ((s_w_id * s_i_id), 10000) = su_suppkey "
					+   "AND ol_w_id = o_w_id "
					+   "AND ol_d_id = o_d_id "
					+   "AND ol_o_id = o_id "
					+   "AND c_id = o_c_id "
					+   "AND c_w_id = o_w_id "
					+   "AND c_d_id = o_d_id "
					+   "AND su_nationkey = n1.n_nationkey "
					+   "AND ascii(substring(c_state ,1  ,1)) = n2.n_nationkey "
					+   "AND ((n1.n_name = 'Germany' "
					+         "AND n2.n_name = 'Cambodia') "
					+        "OR (n1.n_name = 'Cambodia' "
					+            "AND n2.n_name = 'Germany')) "
					+ "GROUP BY su_nationkey, "
					+          "cust_nation, "
					+          "l_year "
					+ "ORDER BY su_nationkey, "
					+          "cust_nation, "
					+          "l_year"
	);
		protected SQLStmt get_query(DatabaseType dbType) {
			if (dbType == DatabaseType.TiSPARK)
			    return tidb_query_stmt;
	    return query_stmt;
	}
}
