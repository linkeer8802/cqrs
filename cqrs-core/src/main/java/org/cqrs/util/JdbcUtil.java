/**
 * Copyright (c) 2017 The original author or authors Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.cqrs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weird
 */
public class JdbcUtil {
  
  static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

  public static Connection getConnection(String url, String username, String password) throws SQLException {
    
    return DriverManager.getConnection(url, username, password);
  }
  
  public static long execute(PreparedStatement stmt) throws SQLException {
    try {
      return stmt.executeUpdate();
    } catch (Exception e) {
      stmt.getConnection().close();
      stmt.close();
      throw e;
    }
  }

  public static ResultSet exexuteQuery(PreparedStatement stmt) throws SQLException {
    try {
      return stmt.executeQuery();
    } catch (Exception e) {
      stmt.getConnection().close();
      stmt.close();
      throw e;
    }
  }
}
