/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.util;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import java.util.Vector;

public class TransientQueue implements Queue {
  private Vector<Object> items;
  public TransientQueue() {
    items = new Vector<Object>();
  }
  public void add(Object o) {
    items.add(o);
  }
  public Object remove() {
    if (items.isEmpty()) {
      return null;
    } else {
      return items.remove(0);
    }
  }
  public Object peek(int n) {
    return items.get(n);
  }
  public boolean isEmpty() {
    return items.isEmpty();
  }
  public int size() {
    return items.size();
  }
}
