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

public class Q2 extends GenericQuery {
	
    public final SQLStmt query_stmt = new SQLStmt(
            "SELECT /*+ tidb_bcj(bmsql_customer,bmsql_new_order,bmsql_oorder,bmsql_order_line) */ ol_o_id,\n" +
					"ol_w_id,\n" +
					"ol_d_id,\n" +
					"sum(ol_amount) AS revenue,\n" +
					"o_entry_d\n" +
					"FROM\n" +
					"bmsql_order_line,\n" +
					"bmsql_oorder,\n" +
					"bmsql_customer,\n" +
					"bmsql_new_order\n" +
					"WHERE c_state LIKE 'A%'\n" +
					"AND c_id = o_c_id\n" +
					"AND c_w_id = o_w_id\n" +
					"AND c_d_id = o_d_id\n" +
					"AND no_w_id = o_w_id\n" +
					"AND no_d_id = o_d_id\n" +
					"AND no_o_id = o_id\n" +
					"AND ol_w_id = o_w_id\n" +
					"AND ol_d_id = o_d_id\n" +
					"AND ol_o_id = o_id\n" +
					"AND o_entry_d > timestamp'2020-05-12 07:00:00'\n" +
					"GROUP BY ol_o_id,\n" +
					"ol_w_id,\n" +
					"ol_d_id,\n" +
					"o_entry_d\n" +
					"ORDER BY revenue DESC , o_entry_d limit 100"
        );
	
		protected SQLStmt get_query(DatabaseType dbType) {
	    return query_stmt;
	}
	public int get_query_index() { return 2;}
}
