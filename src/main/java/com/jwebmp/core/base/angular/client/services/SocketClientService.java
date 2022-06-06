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
@NgImportReference(value = "webSocket", reference = "rxjs/webSocket")
@NgImportReference(value = "retry, RetryConfig", reference = "rxjs/operators")
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
@NgImportReference(value = "ActivatedRoute", reference = "@angular/router")

@NgField("static websocket: any;")
@NgField("static dataListenerMappings = new Map<string, BehaviorSubject<any>>();")

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
          "       SocketClientService.dataListenerMappings.set(listener,observer = new BehaviorSubject<object>({}));" +
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
@NgMethod("send(action:string,data:object, eventType :string,event? : any, component? : ElementRef<any>) : void {\n" +
          "" +
          //"alert('sending...');" +
          "const news : any = {\n" +
          "};\n" +
          "news.data = data;\n" +
          "news.action = action;\n" +
          "news.data.localStorage = window.localStorage;\n" +
          "news.data.sessionStorage = window.sessionStorage;\n" +
          "news.data.parameters = this.getParametersObject();\n" +
          "news.data.hashbang = window.location.hash;\n" +
          "news.data.route = this.routeLocation.path();\n" +
          "news.data.state = this.routeLocation.getState();\n" +
          "news.data.datetime = new Date().getUTCDate();\n" +
          "news.data.eventType = eventType;\n" +
          "news.data.headers = {};\n" +
          "news.data.headers.useragent = navigator.userAgent;\n" +
        //  "news.data.headers.appClassName = EnvironmentModule.appClass;\n" +
          "news.data.headers.cookieEnabled = navigator.cookieEnabled ;\n" +
          "news.data.headers.appName = navigator.appName  ;\n" +
          "news.data.headers.appVersion = navigator.appVersion   ;\n" +
          "news.data.headers.language = navigator.language ;\n" +
          "" +
          "if(event)\n" +
          "{\n" +
          "   news.event = JSON.stringify(event);\n" +
          "}\n" +
          "" +
          "if(component)\n" +
          "{\n" +
          "        let ele = component.nativeElement;\n" +
          "        let attributeNames: string[] =  ele.getAttributeNames();\n" +
          "        let attributes: any = {};\n" +
          "        for (let attr of attributeNames) {\n" +
          "            try {\n" +
          "                attributes[attr] = ele.getAttribute(attr);\n" +
          "            } catch (error) {\n" +
          "                console.log(error);\n" +
          "            }\n" +
          "        }\n" +
          "        news.data.attributes = attributes;\n" +
          "        news.componentId = ele.getAttribute(\"id\");\n" +
          "   }\n" +
          "" +
          //	"alert('news : ' + JSON.stringify(news));" +
          "SocketClientService.websocket.next(news);\n" +
          "}\n")

@NgMethod("processResult(response:any)\n" +
          "{\n" +
          //	"   console.log('message received: ' + JSON.stringify(response));\n" +
          "   if(response.localStorage)\n" +
          "   {\n" +
          "      " +
          //	"       alert('update local storage');" +
          //	"       alert('ttt - ' + typeof response.localStorage);" +
          "       " +
          "      Object.keys(response.localStorage).forEach(prop => {\n" +
          "         window.localStorage.setItem(prop, response.localStorage[prop]);\n" +
          "           });\n" +
          "" +
          "   }\n" +
          "" +
          "   if(response.sessionStorage)\n" +
          "   {\n" +
          "      " +
          //	"       alert('update local storage');" +
          //	"       alert('ttt - ' + typeof response.localStorage);" +
          "       " +
          "      Object.keys(response.sessionStorage).forEach(prop => {\n" +
          "         window.sessionStorage.setItem(prop, response.sessionStorage[prop]);\n" +
          "           });\n" +
          "" +
          "   }\n" +
          "" +
          "if(response.features)\n" +
          "{\n" +
          "   " +
          "}\n" +
          "" +
          "" +
          //	"debugger;" +
          "if(response.reactions)\n" +
          "{\n" +
          "    for(let reaction of response.reactions)\n" +
          "   {\n" +
          "      const react : any = reaction; \n" +
          "      if(\"RedirectUrl\" == react.reactionType)\n" +
          "       {\n" +
          //	"          alert('redirect to - ' + react.reactionMessage);\n" +
          "            this.router.navigateByUrl(react.reactionMessage);\n " +
          "       }\n" +
          "   }\n" +
          "}\n" +
          "" +
          "" +
          //	"debugger;" +
          "if(response.data)\n" +
          "{\n" +
          //     "   alert('data is response'); " +
          // "   for(let d of response.data)\n" +
          //   "   {\n" +
          "      const dMap : any = Object.keys(response.data); " +
          // "       alert('keys returned - ' + dMap);\n" +
          "      for(let key of dMap)" +
          "       {" +
          "           const jsonString = response.data[key];" +
          "           const jsonValue = JSON.parse(jsonString);" +
          //   "               alert('key and subject?' + key + ' - ' + jsonString);" +
          "           const subject = SocketClientService.dataListenerMappings.get(key);" +
          "           if(subject) " +
          "           {" +
          //    "               alert('subject is found - sending notify' + key + ' - ' + jsonString);" +
          "               subject.next(jsonValue);" +
          "           }" +
          //   "       }" +
          "       " +
          "   }\n" +
          "}\n" +
          "" +
          "" +
          "}\n")
@NgProvider
public class SocketClientService<J extends SocketClientService<J>> implements INgProvider<J>
{
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
