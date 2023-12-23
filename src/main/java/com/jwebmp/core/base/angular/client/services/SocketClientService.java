package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.boot.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.util.*;

@NgConstructorParameter(value = "private socketClientService : SocketClientService", onSelf = false, onParent = true)

@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "Location", reference = "@angular/common")
@NgImportReference(value = "ElementRef", reference = "@angular/core")
@NgImportReference(value = "BehaviorSubject", reference = "rxjs/internal/BehaviorSubject")
@NgImportReference(value = "Subject", reference = "rxjs")
@NgImportReference(value = "webSocket", reference = "rxjs/webSocket")
@NgImportReference(value = "retry, RetryConfig", reference = "rxjs/operators")
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
@NgImportReference(value = "ActivatedRoute", reference = "@angular/router")

@NgField("static websocket: any;")
@NgField("static dataListenerMappings = new Map<string, Subject<any>>();")

@NgConstructorParameter("private routeLocation: Location")
@NgConstructorParameter("private router: Router")
@NgConstructorParameter("private route: ActivatedRoute")


@NgConstructorBody(" if(!SocketClientService.websocket) {" +
                   "const subject = webSocket((location.protocol + '//' + location.host).replace('http','ws') + '/wssocket');")
@NgConstructorBody("SocketClientService.websocket = subject;")
@NgConstructorBody("const retryConfig: RetryConfig = {\n" +
                   "  delay: 3000,\n" +
                   "};\n")
@NgConstructorBody("subject.pipe(\n" +
                   "   retry(retryConfig) //support auto reconnect\n" +
                   ").subscribe(\n" +
                   "   msg => this.processResult(msg), \n" +
                   "   err => console.log('websocket error : ',err), \n" +
                   "   () => console.log('complete') \n" +
                   ");")

@NgConstructorBody("}")

@NgMethod("registerListener(listener:string) : any" +
          "{" +
          "   let observer = SocketClientService.dataListenerMappings.get(listener);" +
          "   if(!observer)" +
          "   {" +
          "       SocketClientService.dataListenerMappings.set(listener,observer = new BehaviorSubject<any>(undefined));" +
          "   }" +
          "   return observer;" +
          "}")
@NgMethod("deregisterListener(listener: string): void {\n" +
          "        SocketClientService.dataListenerMappings.delete(listener);\n" +
          "    }")

@NgMethod("getParametersObject() : object {\n" +
          "    try {\n" +
          "        var search = location.search.substring(1);\n" +
          "        return JSON.parse('{\"' + decodeURI(search).replace(/\"/g, '\\\\\"').replace(/&/g, '\",\"').replace(/=/g, '\":\"') + '\"}');\n" +
          "    } catch (err) {\n" +
          "        return {};\n" +
          "    }\n" +
          "}\n")
@NgMethod(//"alert('sending...');" +
//  "news.data.headers.appClassName = EnvironmentModule.appClass;\n" +
//	"alert('news : ' + JSON.stringify(news));" +
        """
                send(action:string,data:object, eventType :string,event? : any, component? : ElementRef<any>) : void {
					const news : any = {
					};
					news.data = data;
					news.action = action;
					news.data.url = window.location;
					news.data.localStorage = window.localStorage;
					news.data.sessionStorage = window.sessionStorage;
					news.data.parameters = this.getParametersObject();
					news.data.hashbang = window.location.hash;
					news.data.route = this.routeLocation.path();
					news.data.state = this.routeLocation.getState();
					news.data.history = history.state;
					news.data.datetime = new Date().getUTCDate();
					news.data.eventType = eventType;
					news.data.headers = {};
					news.data.headers.useragent = navigator.userAgent;
					news.data.headers.cookieEnabled = navigator.cookieEnabled ;
					news.data.headers.appName = navigator.appName  ;
					news.data.headers.appVersion = navigator.appVersion   ;
					news.data.headers.language = navigator.language ;
					if(event)
					{
					   news.event = JSON.stringify(event);
					}
					if(component)
					{
						let ele = component.nativeElement;
						news.componentId = ele.getAttribute("id");
						news.data.attributes = {};
						for (const attributeName of ele.getAttributeNames()) {
							news.data.attributes[attributeName] = ele.getAttribute(attributeName);
						}
					}
					SocketClientService.websocket.next(news);
                }
                """)

@NgMethod(//	"   console.log('message received: ' + JSON.stringify(response));\n" +
//	"       alert('update local storage');" +
//	"       alert('ttt - ' + typeof response.localStorage);" +
//	"       alert('update local storage');" +
//	"       alert('ttt - ' + typeof response.localStorage);" +
//	"debugger;" +
//	"          alert('redirect to - ' + react.reactionMessage);\n" +
//	"debugger;" +
//     "   alert('data is response'); " +
// "   for(let d of response.data)\n" +
//   "   {\n" +
// "       alert('keys returned - ' + dMap);\n" +
//    "           const jsonValue = JSON.parse(jsonString);" +
//   "               alert('key and subject?' + key + ' - ' + jsonString);" +
//    "               alert('subject is found - sending notify' + key + ' - ' + jsonString);" +
//   "       }" +
        """
                processResult(response:any)
                {
                   if(response.localStorage)
                   {
                       Object.keys(response.localStorage).forEach(prop => {
                         window.localStorage.setItem(prop, response.localStorage[prop]);
                       });
                   }
                   if(response.sessionStorage)
                   {
                       Object.keys(response.sessionStorage).forEach(prop => {
                         window.sessionStorage.setItem(prop, response.sessionStorage[prop]);
                        });
                   }
					if(response.features)
					{
					}
					if(response.reactions)
					{
						for(let reaction of response.reactions)
					   {
						  const react : any = reaction;
						  if("RedirectUrl" == react.reactionType)
						   {
								this.router.navigateByUrl(react.reactionMessage);
							}
					   }
					}
					if(response.data)
					{
                      const dMap : any = Object.keys(response.data);
                      for(let key of dMap)
                       {
                           const jsonString = response.data[key];
                           const subject = SocketClientService.dataListenerMappings.get(key);
                           if(subject)
                           {
                               subject.next(jsonString);
                           }
                       }
                	}
                }
                """)
@NgProvider
public class SocketClientService<J extends SocketClientService<J>> implements INgProvider<J>
{
	@Override
	public List<String> globalFields()
	{
		List<String> out = INgProvider.super.globalFields();
		out.add("declare var $:any;");
		return out;
	}
	
	@Override
	public List<String> componentDecorators()
	{
		List<String> out = INgProvider.super.componentDecorators();
		out.add("@Injectable({\n" +
		        "  providedIn: 'any'\n" +
		        "})");
		return out;
	}
}
