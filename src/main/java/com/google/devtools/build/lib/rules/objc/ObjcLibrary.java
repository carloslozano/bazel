// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.objc;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.rules.RuleConfiguredTargetFactory;
import com.google.devtools.build.lib.view.ConfiguredTarget;
import com.google.devtools.build.lib.view.RuleContext;
import com.google.devtools.build.xcode.xcodegen.proto.XcodeGenProtos.BuildSetting;
import com.google.devtools.build.xcode.xcodegen.proto.XcodeGenProtos.DependencyControl;

/**
 * Implementation for the {@code objc_library} and {@code objc_import} rules.
 */
public class ObjcLibrary implements RuleConfiguredTargetFactory {
  @Override
  public ConfiguredTarget create(RuleContext ruleContext) throws InterruptedException {
    ObjcCommon info = ObjcCommon.fromContext(
        ruleContext, ImmutableList.<SdkFramework>of() /* extraSdkFrameworks */);
    info.reportErrors();
    XcodeProvider xcodeProvider = info.xcodeProvider(Optional.<Artifact>absent(),
        ObjcRuleClasses.pchFile(ruleContext),
        ImmutableList.<DependencyControl>of(), ImmutableList.<BuildSetting>of());
    ObjcActionsBuilder.registerAll(
        ruleContext,
        ObjcActionsBuilder.baseActions(ruleContext, info.getObjcProvider(), xcodeProvider));
    return info.configuredTarget(
        NestedSetBuilder.<Artifact>stableOrder()
            .addAll(ObjcRuleClasses.outputAFile(ruleContext).asSet())
            .add(ruleContext.getImplicitOutputArtifact(ObjcRuleClasses.PBXPROJ))
            .build(),
        xcodeProvider);
  }
}