package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgServiceProvider;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;

@NgImportReference(value = "Subscription", reference = "rxjs")
@NgImportReference(value = "BehaviorSubject", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "Injectable", reference = "@angular/core")

@NgField("private subscription?: Subscription;")
@NgField("public additionalData: any = {};")
@NgOnDestroy("this.subscription?.unsubscribe();")

@NgImportReference(value = "OnDestroy", reference = "@angular/core")
//@NgImportReference(value = "Output", reference = "@angular/core")
//@NgImportReference(value = "EventEmitter", reference = "@angular/core")

@NgMethod("""
        /**
             * Deeply merges source into target, mutating target in place.
             * - Primitives: assign if different
             * - Objects: recurse
             * - Arrays: if items are objects with "id", merge by id; otherwise replace only if different by length/content
             * Returns true if any change was applied to target.
             */
            private deepMergeInto(target: any, source: any): boolean {
                if (target === source) return false;
        
                // Handle null/undefined
                if (source === null || source === undefined) {
                    if (target !== source) {
                        // Assigning null/undefined into target position is a change.
                        // Since we mutate in place, the caller must assign at parent level if needed.
                        // Here we indicate a change but do not auto-delete properties to be safe.
                        return false;
                    }
                    return false;
                }
        
                // Primitives and functions: replace if different
                const isObject = (v: any) => v !== null && typeof v === 'object';
                if (!isObject(source) || Array.isArray(source)) {
                    // Arrays handled below, primitives here
                    if (Array.isArray(source)) {
                        return this.mergeArraysInPlace(target, source);
                    } else {
                        if (target !== source) {
                            // For root fields, we need the caller to assign. For nested fields, we set directly.
                            // This helper is designed for object properties/array items, so we return true to tell caller to assign.
                            return true;
                        }
                        return false;
                    }
                }
        
                // Objects
                let changed = false;
                // Ensure target is object
                if (!isObject(target) || Array.isArray(target)) {
                    // Caller must replace with a new object
                    return true;
                }
        
                // Merge existing keys from source
                for (const key of Object.keys(source)) {
                    const sVal = source[key];
                    const tHas = Object.prototype.hasOwnProperty.call(target, key);
                    const tVal = tHas ? target[key] : undefined;
        
                    if (Array.isArray(sVal)) {
                        const didChange = this.mergeArraysInPlace(tVal, sVal);
                        if (didChange) {
                            // If target didn't have this key or wasn't an array, assign new array
                            if (!Array.isArray(tVal)) {
                                target[key] = Array.isArray(sVal) ? [...sVal] : sVal;
                            }
                            changed = true;
                        }
                    } else if (isObject(sVal)) {
                        if (!isObject(tVal) || Array.isArray(tVal)) {
                            target[key] = {};
                            changed = true;
                        }
                        const nestedChanged = this.deepMergeInto(target[key], sVal);
                        if (nestedChanged) changed = true;
                    } else {
                        if (tVal !== sVal) {
                            target[key] = sVal;
                            changed = true;
                        }
                    }
                }
        
                // Remove properties in target that are not present in source
                for (const key of Object.keys(target)) {
                    if (!Object.prototype.hasOwnProperty.call(source, key)) {
                        delete target[key];
                        changed = true;
                    }
                }
        
                return changed;
            }
        
            /**
             * Merge arrays in place:
             * - If items are objects with "id", merge by id (update existing, push new). No removals by default.
             * - Otherwise, replace array only if length or contents differ.
             * Returns true if any change occurred.
             */
            private mergeArraysInPlace(targetArr: any, sourceArr: any[]): boolean {
                if (!Array.isArray(sourceArr)) return false;
        
                // If target is not an array, caller should assign
                if (!Array.isArray(targetArr)) {
                    return true;
                }
        
                // Heuristic: objects with "id" -> merge by id
                const itemsAreObjectsWithId =
                    sourceArr.length > 0 &&
                    typeof sourceArr[0] === 'object' &&
                    sourceArr[0] !== null &&
                    !Array.isArray(sourceArr[0]) &&
                    'id' in sourceArr[0];
        
                if (!itemsAreObjectsWithId) {
                    // For non-id arrays:
                    // - If primitives only, replace if length/content differ
                    // - If contains objects/arrays, replace entire array when any difference is detected to avoid nested duplications
                    const primitivesOnly = sourceArr.every(it => it === null || (typeof it !== 'object'));

                    if (primitivesOnly) {
                        if (targetArr.length !== sourceArr.length) {
                            targetArr.length = 0;
                            for (const item of sourceArr) targetArr.push(item);
                            return true;
                        }
                        let changed = false;
                        for (let i = 0; i < sourceArr.length; i++) {
                            const s = sourceArr[i];
                            const t = targetArr[i];
                            if (t !== s) {
                                targetArr[i] = s;
                                changed = true;
                            }
                        }
                        return changed;
                    } else {
                        // Contains complex items (objects/arrays) -> avoid deep index merges which can duplicate nested children.
                        // Replace entire array if shallow reference or length/content differ
                        let identical = targetArr.length === sourceArr.length;
                        if (identical) {
                            for (let i = 0; i < sourceArr.length; i++) {
                                if (targetArr[i] !== sourceArr[i]) { identical = false; break; }
                            }
                        }
                        if (!identical) {
                            targetArr.length = 0;
                            for (const item of sourceArr) targetArr.push(item);
                            return true;
                        }
                        return false;
                    }
                }
        
                // Merge by id
                const indexById = new Map<any, number>();
                for (let i = 0; i < targetArr.length; i++) {
                    const item = targetArr[i];
                    if (item && typeof item === 'object' && 'id' in item) {
                        if (!indexById.has(item.id)) {
                            indexById.set(item.id, i);
                        }
                    }
                }
        
                let changed = false;
                const seenIds = new Set<any>();
        
                for (const srcItem of sourceArr) {
                    if (!srcItem || typeof srcItem !== 'object' || Array.isArray(srcItem) || !('id' in srcItem)) {
                        // Non-object or missing id inside an id-array branch: skip to avoid accidental duplicates
                        // Such items should be handled by the non-id branch above
                        continue;
                    }
                    const id = (srcItem as any).id;
                    if (seenIds.has(id)) {
                        // Skip duplicate ids in this source payload
                        continue;
                    }
                    seenIds.add(id);
        
                    if (!indexById.has(id)) {
                        // New item -> push (preserves existing positions)
                        targetArr.push(srcItem);
                        changed = true;
                        indexById.set(id, targetArr.length - 1);
                    } else {
                        const idx = indexById.get(id)!;
                        const tgtItem = targetArr[idx];
        
                        // If both are objects, deep merge; otherwise replace
                        if (tgtItem && typeof tgtItem === 'object' && !Array.isArray(tgtItem)) {
                            const nestedChanged = this.deepMergeInto(tgtItem, srcItem);
                            if (nestedChanged) changed = true;
                        } else if (tgtItem !== srcItem) {
                            // Replace the slot but keep the same index to preserve position
                            targetArr[idx] = srcItem;
                            changed = true;
                        }
                    }
                }
        
                // Remove items from target whose ids are not present in the source array
                for (let i = targetArr.length - 1; i >= 0; i--) {
                    const it = targetArr[i];
                    if (it && typeof it === 'object' && !Array.isArray(it) && 'id' in it) {
                        const id = (it as any).id;
                        if (!seenIds.has(id)) {
                            targetArr.splice(i, 1);
                            changed = true;
                        }
                    }
                }
        
                // Deduplicate target by id while preserving the first occurrence's position.
                const firstIndexById = new Map<any, number>();
                for (let i = 0; i < targetArr.length; i++) {
                    const it = targetArr[i];
                    if (it && typeof it === 'object' && !Array.isArray(it) && 'id' in it) {
                        const id = (it as any).id;
                        if (!firstIndexById.has(id)) {
                            firstIndexById.set(id, i);
                        } else {
                            const firstIdx = firstIndexById.get(id)!;
                            const firstItem = targetArr[firstIdx];
                            if (firstItem && typeof firstItem === 'object' && !Array.isArray(firstItem)) {
                                const nestedChanged = this.deepMergeInto(firstItem, it);
                                if (nestedChanged) changed = true;
                            }
                            targetArr.splice(i, 1);
                            changed = true;
                            i--; // adjust after removal
                        }
                    }
                }
        
                return changed;
            }
        
        """)

public interface INgServiceProvider<J extends INgServiceProvider<J>> extends IComponent<J>
{
    default NgServiceProvider getAnnotation()
    {
        return getClass().getAnnotation(NgServiceProvider.class);
    }

    @Override
    default List<NgImportReference> getAllImportAnnotations()
    {
        List<NgImportReference> out = IComponent.super.getAllImportAnnotations();
        NgComponentReference reference = getNgComponentReference(getAnnotation().value());
        out.addAll(putRelativeLinkInMap(getClass(), reference));
        NgComponentReference reference2 = getNgComponentReference(getAnnotation().dataType());
        out.addAll(putRelativeLinkInMap(getClass(), reference2));

        var reference3 = AnnotationUtils.getNgImportReference("inject", "@angular/core");
        out.add(reference3);

        return out;
    }

    @Override
    default List<String> constructorParameters()
    {
        List<String> out = IComponent.super.constructorParameters();
        //  out.add("private service : " + getAnnotation().value()
        //          .getSimpleName());
        return out;
    }

    @Override
    default List<String> decorators()
    {
        List<String> out = IComponent.super.decorators();
        out.add("@Injectable({\n" +
                "  providedIn: '" + providedIn() + "'\n" +
                "})");
        return out;
    }

    @Override
    default List<String> fields()
    {
        List<String> out = IComponent.super.fields();
        out.add("private _onUpdate = new BehaviorSubject<boolean>(false);");
        out.add("private readonly service = inject(" + getAnnotation().value()
                                                                      .getSimpleName() + ");");

        if (!getAnnotation().dataArray())
        {
            out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
                                                                                           .getSimpleName() + " = " + INgDataType.renderObjectStructure(getAnnotation().dataType()) + ";");


        }
        else
        {
            out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
                                                                                           .getSimpleName() + "[] = [];");

        }
        return out;
    }

    @Override
    default List<String> constructorBody()
    {
        List<String> out = IComponent.super.constructorBody();
        //        "                    this." + getAnnotation().variableName() + " = JSON.parse(message as any);\n" +
        /*		     "" +
		     "" +
		     "            if (message && observer.out) {\n";
		s += "                this." + getAnnotation().variableName() + " = message.out[0];\n";
		s += "                this._onUpdate.next(true);\n" +
		     "            }\n" +*/

        if (getAnnotation().deepMerge())
        {
            String s = """
                    \tthis.subscription = this.service.data
                            %s%s.subscribe(message => {
                                if (message) {
                                const incoming = typeof message === 'string' ? JSON.parse(message as any) : (message as any);
                                const changed = this.deepMergeInto(this.%s, incoming);
                                if(changed)
                                    this._onUpdate.next(true);
                                }
                            });
                    """.formatted(buffer() ? ".pipe(bufferTime(" + bufferTime() + "))" : "",
                    takeLast() ? ".pipe(takeLast(" + takeLastCount() + "))" : "",
                    getAnnotation().variableName());
            out.add(s);
        }
        else
        {
            String s = """
                    \tthis.subscription = this.service.data
                            %s%s.subscribe(message => {
                                if (message) {
                                    if (typeof message === 'string')
                                            this.%s = JSON.parse(message as any);
                                        else this.%s = message as any;
                                    this._onUpdate.next(true);
                                }
                            });
                    """.formatted(buffer() ? ".pipe(bufferTime(" + bufferTime() + "))" : "",
                    takeLast() ? ".pipe(takeLast(" + takeLastCount() + "))" : "",
                    getAnnotation().variableName(), getAnnotation().variableName());
            out.add(s);
        }
        //out.add("this.checkData();");
        return out;
    }

    @Override
    default List<String> methods()
    {
        List<String> out = IComponent.super.methods();
        String sendDataString = "\tpublic sendData(datas : any){\n";
        sendDataString += "\t\tthis.service.additionalData = this.additionalData;\n" +
                "\t\tthis.service.sendData(datas);\n" +
                "\t}";

        out.add(sendDataString);

        out.add("\tget onUpdate(): Observable<boolean> {\n" +
                "\t\treturn this._onUpdate.asObservable();\n" +
                "\t}");
        out.add("\tcheckData()\n" +
                "\t{\n" +
                "\t\tthis.service.fetchData();\n" +
                "\t}");

        String resetString = "\treset() {\n" +
                "\t\tthis._onUpdate.next(false);\n" +
                "\t\tthis.service.additionalData = {};\n" +
                "\t\tthis.service.additionalData = this.additionalData;\n";
        if (!getAnnotation().dataArray())
        {
            resetString += "\t\tthis." + getAnnotation().variableName() + " = " + INgDataType.renderObjectStructure(getAnnotation().dataType()) + ";";
        }
        else
        {
            resetString += "\t\tthis." + getAnnotation().variableName() + " = [];";

        }
        resetString += "\t}\n";
        out.add(resetString);
        return out;
    }

    default String providedIn()
    {
        if (getClass().isAnnotationPresent(NgServiceProvider.class))
        {
            var ng = getClass().getAnnotation(NgServiceProvider.class);
            if (ng.singleton())
            {
                return "root";
            }
        }
        return "any";
    }

    @Override
    default List<String> interfaces()
    {
        List<String> out = IComponent.super.interfaces();
        out.add("OnDestroy");
        //out.add("OnInit");
        return out;
    }

    @Override
    default String renderOnDestroyMethod()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderOnDestroyMethod());
        out.append("ngOnDestroy() {\n");
        for (String s : onDestroy())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
        List<NgOnDestroy> fInit = IGuiceContext.get(AnnotationHelper.class)
                                               .getAnnotationFromClass(getClass(), NgOnDestroy.class);
        fInit.sort(Comparator.comparingInt(NgOnDestroy::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgOnDestroy ngField : fInit)
            {
                outs.add(ngField.value()
                                .trim());
            }
        }
        StringBuilder fInitOut = new StringBuilder();
        for (String s : outs)
        {
            fInitOut.append(s)
                    .append("\n");
        }
        out.append("\t")
           .append(fInitOut)
           .append("\n");
        out.append("}\n");
        return out.toString();
    }


    default boolean buffer()
    {
        return false;
    }

    default int bufferTime()
    {
        return 500;
    }

    default boolean takeLast()
    {
        return false;
    }

    default int takeLastCount()
    {
        return 100;
    }
}
