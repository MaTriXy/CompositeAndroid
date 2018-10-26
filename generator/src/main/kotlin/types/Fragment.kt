package types

import inPath
import parse.AnalyzedJavaFile
import parse.parseJavaFile
import writer.writeComposite
import writer.writeDelegate
import writer.writeInterface
import writer.writePlugin
import java.io.File

private val outPackage = "fragment"

private val outPath = "../fragment/src/main/java/com/pascalwelsch/compositeandroid/"

fun main(args: Array<String>) {
    generateFragments()
}

fun generateFragments() {

    val fragment = parseJavaFile(File("$inPath/BlueprintFragment.java"))

    generateFragment(fragment)

    generateDialogFragment(fragment)
}


val replaceAmbitiousTypesWithFullPackageNames: (String) -> String = {
    it.replace(" SavedState", " Fragment.SavedState")
            .replace("(SavedState)", "(Fragment.SavedState)")
            .replace("<SavedState", "<Fragment.SavedState")
            .replace(" Lifecycle", " android.arch.lifecycle.Lifecycle")
            .replace("(Lifecycle)", "(android.arch.lifecycle.Lifecycle)")
            .replace("<Lifecycle", "<android.arch.lifecycle.Lifecycle")
}

private fun generateDialogFragment(fragment: AnalyzedJavaFile) {
    val dialogfragment = parseJavaFile(File("$inPath/BlueprintDialogFragment.java"))

    writeComposite(outPath,
            dialogfragment,
            outPackage,
            "CompositeDialogFragment",
            "DialogFragment implements ICompositeDialogFragment",
            additionalImports = """
            |import android.support.v4.app.*;
            |import android.app.Activity;
            |import java.io.*;
            |import android.content.*;
            |import android.view.*;
            |import android.view.animation.*;
            |import android.util.*;
            |import android.content.res.*;
            |import android.animation.Animator;
            |import android.view.ContextMenu.ContextMenuInfo;
            |import android.arch.lifecycle.ViewModelStore;
            |import android.arch.lifecycle.Lifecycle;
            |import android.content.IntentSender.SendIntentException;
            |import android.support.v4.app.SupportActivity.ExtraData;
            |import android.arch.lifecycle.LiveData;
            """.replaceIndentByMargin(),
            transform = replaceAmbitiousTypesWithFullPackageNames,
            superClassPluginNames = listOf("FragmentPlugin"),
            superClassDelegateName = "FragmentDelegate",
            pluginClassName = "DialogFragmentPlugin",
            delegateClassName = "DialogFragmentDelegate",
            superClassInputFile = fragment)

    writeDelegate(outPath,
            dialogfragment,
            outPackage,
            "DialogFragmentDelegate",
            "ICompositeDialogFragment",
            "DialogFragmentPlugin",
            additionalImports = """
            |import android.support.v4.app.*;
            |import android.app.Activity;
            |import java.io.*;
            |import android.content.*;
            |import android.view.*;
            |import android.view.animation.*;
            |import android.util.*;
            |import android.content.res.*;
            |import java.util.ListIterator;
            |import android.content.IntentSender.SendIntentException;
            |import android.view.ContextMenu.ContextMenuInfo;
            |import android.arch.lifecycle.ViewModelStore;
            |import android.support.v4.app.Fragment.SavedState;
            |import android.animation.Animator;
            |import android.arch.lifecycle.Lifecycle;
            |import android.support.v4.app.SupportActivity.ExtraData;
            |import android.arch.lifecycle.LiveData;
            """.replaceIndentByMargin(),
            transform = replaceAmbitiousTypesWithFullPackageNames,
            extends = "AbstractDelegate<ICompositeDialogFragment, DialogFragmentPlugin>",
            superClassPluginName = "FragmentPlugin",
            superClassDelegateName = "FragmentDelegate",
            superClassInputFile = fragment)

    writePlugin(outPath,
            "DialogFragment",
            dialogfragment,
            outPackage,
            "DialogFragmentPlugin",
            transform = replaceAmbitiousTypesWithFullPackageNames,
            superClassInputFile = fragment,
            extends = "FragmentPlugin")

    writeInterface(outPath,
            dialogfragment,
            outPackage,
            "ICompositeDialogFragment",
            "ICompositeFragment",
            transform = replaceAmbitiousTypesWithFullPackageNames)
}

private fun generateFragment(fragment: AnalyzedJavaFile) {
    writeComposite(outPath,
            fragment,
            outPackage,
            "CompositeFragment",
            "Fragment implements ICompositeFragment",
            additionalImports = """
            |import android.support.v4.app.*;
            |import android.support.v4.app.Fragment.SavedState;
            |import android.arch.lifecycle.ViewModelStore;
            |import android.support.v4.app.SupportActivity.ExtraData;
            |import android.arch.lifecycle.LiveData;
            """.replaceIndentByMargin(),
            transform = replaceAmbitiousTypesWithFullPackageNames,
            delegateClassName = "FragmentDelegate",
            pluginClassName = "FragmentPlugin", superClassInputFile = fragment)

    writeDelegate(outPath,
            fragment,
            outPackage,
            "FragmentDelegate",
            "ICompositeFragment",
            "FragmentPlugin",
            additionalImports = """
            |import android.support.v4.app.*;
            |import java.util.ListIterator;
            |import android.support.v4.app.Fragment.SavedState;
            |import android.arch.lifecycle.Lifecycle;
            |import android.support.v4.app.SupportActivity.ExtraData;
            |import android.arch.lifecycle.LiveData;
            """.replaceIndentByMargin(),
            extends = "AbstractDelegate<ICompositeFragment, FragmentPlugin>",
            transform = replaceAmbitiousTypesWithFullPackageNames)

    writePlugin(outPath,
            "Fragment",
            fragment,
            outPackage,
            "FragmentPlugin",
            additionalImports = """
            |import android.support.v4.app.*;
            |import android.support.v4.app.Fragment.SavedState;
            |import android.support.v4.app.SupportActivity.ExtraData;
            """.replaceIndentByMargin(),
            transform = replaceAmbitiousTypesWithFullPackageNames,
            superClassInputFile = fragment,
            extends = "AbstractPlugin<Fragment, FragmentDelegate>")

    writeInterface(outPath,
            fragment,
            outPackage,
            "ICompositeFragment",
            additionalImports = """
            |import android.support.v4.app.Fragment.SavedState;
            |import android.support.v4.app.SupportActivity.ExtraData;
            """.replaceIndentByMargin(),
            transform = replaceAmbitiousTypesWithFullPackageNames)
}