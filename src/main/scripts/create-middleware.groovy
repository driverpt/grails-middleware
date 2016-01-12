/* Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.codegen.model.Model

/**
 * @author fpape
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */

description 'Creates a Middleware class to become a part of the HTTP Request Pipeline', {
    usage '''
grails create-middleware [MIDDLEWARE CLASS NAME]
Example: grails create-middleware com.yourapp.AuthenticationMiddleware
'''

    argument name: 'Middleware class name', description: 'The Middleware class full name with package'
}

String fullClassName = args[0]
Model model = model(fullClassName)

addStatus "\nCreating middleware class $fullClassName"

render template: template('Middleware.groovy.template'),
        destination: file("grails-app/middleware/$model.packagePath/${model.simpleName}.groovy"),
        model: model, overwrite: false