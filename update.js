#!/usr/bin/env node
const fs = require('fs-extra')
const path = require('path')

const RUNELITE_PLUGIN_PATH =
  '../runelite/runelite-client/src/main/java/net/runelite/client/plugins/timetracking'
const RUNELITE_COPY_PATH = 'src/main/java/com/timetrackingreminder/runelite/'

function patchCopiedFiles(search, replace) {
  const files = getDirectoryFiles(RUNELITE_COPY_PATH)

  for (const file of files) {
    let content = fs.readFileSync(file, 'utf-8')
    content = content.replace(search, replace)
    fs.writeFileSync(file, content, 'utf-8')
  }
}

function getDirectoryFiles(directory) {
  const entries = fs.readdirSync(directory, { withFileTypes: true })

  const files = entries.map((entry) => {
    const entryPath = path.resolve(directory, entry.name)

    if (entry.isFile() && !entry.name.endsWith('.java')) return null
    return entry.isDirectory() ? getDirectoryFiles(entryPath) : entryPath
  })

  return files.filter(Boolean).flat()
}

console.log('Creating RuneLite copy directories')
fs.mkdirpSync(`${RUNELITE_COPY_PATH}/hunter/`, { recursive: true })
fs.mkdirpSync(`${RUNELITE_COPY_PATH}/farming/`, { recursive: true })

console.log('Copying files')
const copyOptions = { overwrite: true }
fs.copySync(`${RUNELITE_PLUGIN_PATH}/hunter/`, `${RUNELITE_COPY_PATH}/hunter/`, copyOptions)
fs.copySync(`${RUNELITE_PLUGIN_PATH}/farming/`, `${RUNELITE_COPY_PATH}/farming/`, copyOptions)

console.log('Patching files: (1) Overwrite package')
patchCopiedFiles(
  'package net.runelite.client.plugins.timetracking.',
  'package com.timetrackingreminder.runelite.'
)

console.log('Patching files: (2) Remove automatic injection')
patchCopiedFiles(/\t*@Inject\n/g, '')
patchCopiedFiles(/\t*@Singleton\n/g, '')

console.log('Patching files: (3) Overwrite visibility')
patchCopiedFiles('access = AccessLevel.PACKAGE', '')
patchCopiedFiles('private BirdHouseTracker(', 'public BirdHouseTracker(')
patchCopiedFiles('private void updateCompletionTime', 'public void updateCompletionTime')
patchCopiedFiles('private FarmingTracker(', 'public FarmingTracker(')
patchCopiedFiles('class FarmingWorld', 'public class FarmingWorld')
patchCopiedFiles('FarmingWorld(', 'public FarmingWorld(')
patchCopiedFiles('private void handleContractState', 'public void handleContractState')
patchCopiedFiles('class FarmingPatch', 'public class FarmingPatch')
patchCopiedFiles('class PatchPrediction', 'public class PatchPrediction')
patchCopiedFiles('\tFarmingRegion(', '\tpublic FarmingRegion(')

const FarmingContractManagerConstructor = `	public FarmingContractManager(Client client, ItemManager itemManager, ConfigManager configManager, TimeTrackingConfig config, FarmingWorld farmingWorld, FarmingTracker farmingTracker)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.configManager = configManager;
		this.config = config;
		this.farmingWorld = farmingWorld;
		this.farmingTracker = farmingTracker;
	}

`
patchCopiedFiles('	public void setContract', FarmingContractManagerConstructor + '	public void setContract')

console.log('Patching files: (4) Remove config write calls')
patchCopiedFiles(
  /configManager\.setRSProfileConfiguration\(.*?\);/g,
  '// configManager.setRSProfileConfiguration call removed. This code path should never be executed, but just in case.'
)

console.log('Patching files: (5) Add auto-generation comment')
patchCopiedFiles(
  '/*\n * Copyright',
  '// THIS FILE WAS AUTOMATICALLY GENERATED. DO NOT EDIT IT MANUALLY. SEE README.\n\n/*\n * Copyright'
)
