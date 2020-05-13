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

public class Q4 extends GenericQuery {

	public int get_query_index() { return 4;}
    public final SQLStmt query_stmt = new SQLStmt(
            "SELECT /*+ tidb_bcj(bmsql_customer,bmsql_oorder,bmsql_order_line) */ c_id,\n" +
					"c_last,\n" +
					"sum(ol_amount) AS revenue,\n" +
					"c_city,\n" +
					"c_phone\n" +
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
					"AND o_entry_d >= timestamp'2020-05-12 07:00:00'\n" +
					"AND o_entry_d = ol_delivery_d\n" +
					"AND c_state LIKE 'A%'\n" +
					"GROUP BY c_id,\n" +
					"c_last,\n" +
					"c_city,\n" +
					"c_phone\n" +
					"ORDER BY revenue DESC\n" +
					"limit 100"
        );
	
		protected SQLStmt get_query(DatabaseType dbType) {
	    return query_stmt;
	}
}
