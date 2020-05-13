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

package com.oltpbenchmark.benchmarks.chbenchmark;

import java.sql.SQLException;
import java.sql.Statement;

import com.oltpbenchmark.api.Procedure.UserAbortException;
import com.oltpbenchmark.api.TransactionType;
import com.oltpbenchmark.api.Worker;
import com.oltpbenchmark.benchmarks.chbenchmark.queries.GenericQuery;
import com.oltpbenchmark.types.DatabaseType;
import com.oltpbenchmark.types.TransactionStatus;
import org.apache.log4j.Logger;

public class CHBenCHmarkWorker extends Worker<CHBenCHmark> {
	private static final Logger LOG = Logger.getLogger(CHBenCHmarkWorker.class);
	public CHBenCHmarkWorker(CHBenCHmark benchmarkModule, int id) throws SQLException {
		super(benchmarkModule, id);
		if (benchmarkModule.getWorkloadConfiguration().getDBType() == DatabaseType.TiDB) {
			// set storage type if needed
			Statement stmt = conn.createStatement();
			stmt.execute("set @@global.tidb_txn_mode='optimistic'");
			stmt.execute("set @@global.tidb_skip_isolation_level_check=1");
			stmt.execute("set @@global.tidb_allow_batch_cop=1");
			stmt.execute("set @@global.tidb_opt_broadcast_join=1");
			if (benchmarkModule.getWorkloadConfiguration().getDBStorageType().toLowerCase().equals("tikv")) {
				stmt.execute("set tidb_isolation_read_engines=\"tikv\"");
			} else if (benchmarkModule.getWorkloadConfiguration().getDBStorageType().toLowerCase().equals("tiflash")) {
				stmt.execute("set tidb_isolation_read_engines=\"tiflash\"");
			}
			stmt.close();
		}
	}
	
	@Override
	protected TransactionStatus executeWork(TransactionType nextTransaction) throws UserAbortException, SQLException {
		GenericQuery proc = null;
		long start_time = System.currentTimeMillis();
		boolean success = false;
	    try {
			try {
				proc = (GenericQuery) this.getProcedure(nextTransaction.getProcedureClass());
				proc.setOwner(this);
				proc.run(conn);
			} catch (ClassCastException e) {
				System.err.println("We have been invoked with an INVALID transactionType?!");
				throw new RuntimeException("Bad transaction type = " + nextTransaction);
			}
			conn.commit();
			success = true;
			return (TransactionStatus.SUCCESS);
		} finally {
	    	if (success)
				LOG.info("XXXXXXX Execute query " + proc.get_query_index() + " cost " + (System.currentTimeMillis() - start_time) + " ms");
	    	else
				LOG.info("XXXXXXX Execute failed for query " + (proc == null ? -1 : proc.get_query_index()));
		}

	}
}
