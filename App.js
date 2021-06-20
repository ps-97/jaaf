import React, { useState, useEffect } from "react";
import { FlatList, SafeAreaView, StatusBar, StyleSheet, Text, TouchableOpacity } from "react-native";
import PackagesModule from "./native_modules/PackagesModule"

const Item = ({ item, onPress, backgroundColor, textColor }) => (
  <TouchableOpacity onPress={onPress} style={[styles.item, backgroundColor]}>
  <Text style={[styles.title, textColor]}>{item.name}</Text>
  <Text style={[styles.subtitle, textColor]}>{item.packagename}</Text>
  </TouchableOpacity>
);

const App = () => {
  const [selectedPackages, setSelectedPackages] = useState(new Set());
  const [packageInfo, setPackageInfo] = useState([]);

  useEffect(() => {
  PackagesModule.listPackagesEvent((source) => {
    setPackageInfo(source);
  })}, [])

  const renderItem = ({ item }) => {
    const backgroundColor = selectedPackages.has(item.packagename) ? "#778899" : "black";
    const color = selectedPackages.has(item.packagename) ? 'white' : '#a9a9a9';

    return (
      <Item
      item={item}
     onPress={() => {
        setSelectedPackages(selectedPackages => {
          let prev_state = new Set(selectedPackages)
          if (prev_state.has(item.packagename)) {
            prev_state.delete(item.packagename)
            PackagesModule.enablePackageEvent(item.packagename);
          } else {
            prev_state.add(item.packagename)
            PackagesModule.disablePackageEvent(item.packagename);
          }
          return prev_state
        })
     }}
      backgroundColor={{ backgroundColor }}
      textColor={{ color }}
      />
    );
  };

  return (
    <SafeAreaView style={styles.container}>
    <FlatList
    data={packageInfo}
    renderItem={renderItem}
    keyExtractor={(item) => item.packagename}
    extraData={selectedPackages}
    />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 0,
    backgroundColor: "black",
  },
  item: {
    padding: 10,
    marginVertical: 2,
    marginHorizontal: 16,
    borderStyle: "solid",
    borderWidth: 1,
    borderColor: "#a9a9a9",
  },
  title: {
    fontSize: 32,
  },
  subtitle: {
    fontSize: 15,
  }
});

export default App;
