/*
 *  Copyright 2013~2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package nl.pocos.applib.editrank;

import org.apache.isis.applib.annotation.Programmatic;

public interface Sortable {

	@Programmatic
	void updateRank(int newRank);
	
	/**
	 * Level of indentation. Indentation can be used to create a 'visible only' structure
	 */	
	@Programmatic
	int getLevel();
	
	@Programmatic
	void increaseIndentation();
	
	@Programmatic
	void decreaseIndentation();
}
